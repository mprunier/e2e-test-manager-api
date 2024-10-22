package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.SCREENSHOT_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestScreenshotEntity;
import fr.njj.galaxion.endtoendtesting.service.CompletePipelineService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobArtifactsService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.SearchSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RecordResultPipelineUseCase {

  private final SearchSuiteRetrievalService searchSuiteRetrievalService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final CompletePipelineService completePipelineService;

  private final PipelineRetrievalService pipelineRetrievalService;
  private final RetrieveGitlabJobArtifactsService retrieveGitlabJobArtifactsService;

  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;

  @Transactional
  public void execute(String pipelineId, String jobId, GitlabJobStatus status) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
    var isAllTestsRun =
        PipelineType.ALL.equals(pipeline.getType())
            || PipelineType.ALL_IN_PARALLEL.equals(pipeline.getType());
    var environment = pipeline.getEnvironment();

    var environmentId = pipeline.getEnvironment().getId();
    try {
      if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
        var artifactData =
            retrieveGitlabJobArtifactsService.getArtifactData(
                environment.getToken(), environment.getProjectId(), jobId);
        if (artifactData.getReport() != null
            && artifactData.getReport().getResults() != null
            && !artifactData.getReport().getResults().isEmpty()) {
          generateReport(artifactData, environmentId, pipeline.getConfigurationTestIdsFilter());
          completePipelineService.execute(pipeline.getId(), ReportPipelineStatus.FINISH);
        } else {
          completePipelineService.execute(pipeline.getId(), ReportPipelineStatus.NO_REPORT_ERROR);
        }
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        completePipelineService.execute(pipeline.getId(), ReportPipelineStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error("Error while recording pipeline result.", e);
      completePipelineService.execute(pipeline.getId(), ReportPipelineStatus.SYSTEM_ERROR);
    } finally {
      updateFinalMetricsEvent.fire(
          UpdateFinalMetricsEvent.builder()
              .environmentId(environment.getId())
              .isAllTestsRun(isAllTestsRun)
              .build());
    }
  }

  @Transactional
  public void generateReport(
      ArtifactDataInternal artifactData,
      long environmentId,
      List<String> configurationTestIdsFilter) {
    var screenshots = artifactData.getScreenshots();
    var report = artifactData.getReport();
    var results = report.getResults();
    results.forEach(
        result -> {
          var file = result.getFile().replaceAll(START_PATH, "");
          processTestsWithoutSuite(
              environmentId, file, configurationTestIdsFilter, result.getTests(), screenshots);
          processSuites(
              environmentId,
              file,
              configurationTestIdsFilter,
              result.getSuites(),
              null,
              screenshots);
        });
  }

  private void processTestsWithoutSuite(
      long environmentId,
      String file,
      List<String> configurationTestIdsFilter,
      List<MochaReportTestInternal> tests,
      Map<String, byte[]> screenshots) {
    if (tests != null) {
      tests.forEach(
          mochaTest -> {
            var configurationSuiteOptional =
                searchSuiteRetrievalService.getBy(environmentId, file, NO_SUITE, null);
            if (configurationSuiteOptional.isPresent()) {
              var configurationTestOptional =
                  configurationTestRetrievalService.getBy(
                      environmentId, file, mochaTest.getTitle(), configurationSuiteOptional.get());
              configurationTestOptional.ifPresent(
                  configurationTestEntity -> {
                    if (configurationTestIdsFilter == null
                        || configurationTestIdsFilter.contains(
                            configurationTestEntity.getId().toString())) {
                      saveTest(mochaTest, configurationTestEntity, screenshots);
                    }
                  });
            }
          });
    }
  }

  private void processSuites(
      long environmentId,
      String file,
      List<String> configurationTestIdsFilter,
      List<MochaReportSuiteInternal> suites,
      ConfigurationSuiteEntity parentSuite,
      Map<String, byte[]> screenshots) {
    if (suites != null) {
      suites.forEach(
          mochaSuite -> {
            var configurationSuiteOptional =
                searchSuiteRetrievalService.getBy(
                    environmentId,
                    file,
                    mochaSuite.getTitle(),
                    parentSuite != null ? parentSuite.getId() : null);
            if (configurationSuiteOptional.isPresent()) {
              processTests(
                  environmentId,
                  file,
                  configurationTestIdsFilter,
                  mochaSuite.getTests(),
                  configurationSuiteOptional.get(),
                  screenshots);
              processSuites(
                  environmentId,
                  file,
                  configurationTestIdsFilter,
                  mochaSuite.getSuites(),
                  configurationSuiteOptional.get(),
                  screenshots);
            }
          });
    }
  }

  private void processTests(
      long environmentId,
      String file,
      List<String> configurationTestIdsFilter,
      List<MochaReportTestInternal> tests,
      ConfigurationSuiteEntity suite,
      Map<String, byte[]> screenshots) {
    if (tests != null) {
      tests.forEach(
          mochaTest -> {
            var configurationTestOptional =
                configurationTestRetrievalService.getBy(
                    environmentId, file, mochaTest.getTitle(), suite);
            configurationTestOptional.ifPresent(
                configurationTestEntity -> {
                  if (configurationTestIdsFilter == null
                      || configurationTestIdsFilter.contains(
                          configurationTestEntity.getId().toString())) {
                    saveTest(mochaTest, configurationTestEntity, screenshots);
                  }
                });
          });
    }
  }

  private void saveTest(
      MochaReportTestInternal mochaTest,
      ConfigurationTestEntity configurationTest,
      Map<String, byte[]> screenshots) {

    var status = getConfigurationStatus(mochaTest);
    var test =
        TestEntity.builder()
            .configurationTest(configurationTest)
            .status(status)
            .errorMessage(mochaTest.getErr() != null ? mochaTest.getErr().getMessage() : null)
            .errorStacktrace(mochaTest.getErr() != null ? mochaTest.getErr().getEstack() : null)
            .code(mochaTest.getCode())
            .duration(mochaTest.getDuration())
            .createdBy("Scheduler")
            .build();
    try {
      var contextList = mochaTest.getContextParse();
      if (contextList != null) {
        contextList.stream()
            .filter(item -> "reference".equals(item.getTitle()))
            .findFirst()
            .ifPresent(
                mochaReportContextInternal ->
                    test.setReference(mochaReportContextInternal.getValue()));
        contextList.stream()
            .filter(item -> "urlError".equals(item.getTitle()))
            .findFirst()
            .ifPresent(
                mochaReportContextInternal ->
                    test.setErrorUrl(mochaReportContextInternal.getValue()));
      }
    } catch (JsonProcessingException e) {
      test.setReference("No Reference");
    }
    test.persist();

    createTestScreenshot(mochaTest, screenshots, test);
  }

  private static ConfigurationStatus getConfigurationStatus(MochaReportTestInternal mochaTest) {
    if (Boolean.TRUE.equals(mochaTest.getPass())) {
      return ConfigurationStatus.SUCCESS;
    } else if (Boolean.TRUE.equals(mochaTest.getPending())
        || Boolean.TRUE.equals(mochaTest.getSkipped())) {
      return ConfigurationStatus.SKIPPED;
    }
    return ConfigurationStatus.FAILED;
  }

  private void createTestScreenshot(
      MochaReportTestInternal mochaTest, Map<String, byte[]> screenshots, TestEntity test) {
    try {
      var contextList = mochaTest.getContextParse();
      if (contextList != null) {
        var screenshotError =
            contextList.stream()
                .filter(item -> "screenshotError".equals(item.getTitle()))
                .findFirst();
        screenshotError.ifPresent(
            mochaReportContextInternal ->
                handleScreenshot(mochaReportContextInternal.getValue(), screenshots, test));
      }
    } catch (JsonProcessingException e) {
      log.info("Screenshot not found on test id [{}]", test.getId());
    }
  }

  private void handleScreenshot(
      String screenshotFilename, Map<String, byte[]> screenshots, TestEntity test) {
    byte[] screenshot = screenshots.get(screenshotFilename);

    if (screenshot != null) {
      TestScreenshotEntity.builder()
          .test(test)
          .filename(screenshotFilename.replace(SCREENSHOT_PATH, ""))
          .screenshot(screenshot)
          .build()
          .persist();
    }
    //    else {
    //      var modifiedScreenshotFilename = removeTextBetweenSlashes(screenshotFilename);
    //      if (!modifiedScreenshotFilename.equals(screenshotFilename)) {
    //        handleScreenshot(modifiedScreenshotFilename, screenshots, test);
    //      }
    //    }
  }

  //  private static String removeTextBetweenSlashes(String input) {
  //    int firstSlash = indexOfNthSlash(input, 2);
  //    int secondSlash = indexOfNthSlash(input, 3);
  //
  //    if (firstSlash == -1 || secondSlash == -1) {
  //      return input;
  //    }
  //
  //    String before = input.substring(0, firstSlash);
  //    String after = input.substring(secondSlash);
  //
  //    return before + after;
  //  }
  //
  //  private static int indexOfNthSlash(String input, int n) {
  //    int index = -1;
  //    while (n > 0 && index < input.length() - 1) {
  //      index = input.indexOf("/", index + 1);
  //      n--;
  //    }
  //    return index;
  //  }
}
