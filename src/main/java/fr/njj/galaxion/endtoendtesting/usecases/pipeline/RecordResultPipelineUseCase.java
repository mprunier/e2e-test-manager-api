package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.SCREENSHOT_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.TestRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportResultInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestScreenshotEntity;
import fr.njj.galaxion.endtoendtesting.service.CompleteAllTestsRunService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobArtifactsService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.SearchSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.TestRetrievalService;
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

  private final GenerateTestReportUseCase generateTestReportUseCase;
  private final CompleteAllTestsRunService completeAllTestsRunService;

  private final PipelineRetrievalService pipelineRetrievalService;
  private final TestRetrievalService testRetrievalService;
  private final RetrieveGitlabJobArtifactsService retrieveGitlabJobArtifactsService;
  private final SearchSuiteRetrievalService searchSuiteRetrievalService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;

  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;
  private final Event<TestRunCompletedEvent> testRunCompletedEvent;

  @Monitored(logExit = false)
  @Transactional
  public void execute(String pipelineId, String jobId, GitlabJobStatus status) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
    var environment = pipeline.getEnvironment();

    var isAllTestsRun = pipeline.getTestIds() == null;

    if (isAllTestsRun) {
      globalUpdate(pipeline, status, environment, jobId);
    } else {
      partialUpdate(jobId, status, pipeline, environment);
    }
    pipeline.setStatus(PipelineStatus.FINISH);
    updateFinalMetricsEvent.fire(
        UpdateFinalMetricsEvent.builder()
            .environmentId(environment.getId())
            .isAllTestsRun(isAllTestsRun)
            .build());
  }

  private void globalUpdate(
      PipelineEntity pipeline,
      GitlabJobStatus status,
      EnvironmentEntity environment,
      String jobId) {
    var environmentId = pipeline.getEnvironment().getId();
    try {
      if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
        var artifactData =
            retrieveGitlabJobArtifactsService.getArtifactData(
                environment.getToken(), environment.getProjectId(), jobId);
        if (artifactData.getReport() != null
            && artifactData.getReport().getResults() != null
            && !artifactData.getReport().getResults().isEmpty()) {
          report(artifactData, environmentId);
          completeAllTestsRunService.complete(environmentId, null);
        } else {
          completeAllTestsRunService.complete(
              environmentId, ReportAllTestRanStatus.NO_REPORT_ERROR);
        }
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        completeAllTestsRunService.complete(environmentId, ReportAllTestRanStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      completeAllTestsRunService.complete(environmentId, ReportAllTestRanStatus.SYSTEM_ERROR);
    }
  }

  private void partialUpdate(
      String jobId,
      GitlabJobStatus status,
      PipelineEntity pipeline,
      EnvironmentEntity environment) {
    var testIds = pipeline.getTestIds().stream().map(Long::valueOf).toList();
    var tests = testRetrievalService.getAll(testIds);

    try {
      if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
        var artifactData =
            retrieveGitlabJobArtifactsService.getArtifactData(
                environment.getToken(), environment.getProjectId(), jobId);
        generateTestReportUseCase.execute(artifactData, tests);
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        updateStatus(tests, ConfigurationStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR);
    } finally {
      testRunCompletedEvent.fire(
          TestRunCompletedEvent.builder().environmentId(environment.getId()).build());
    }
  }

  public void report(ArtifactDataInternal artifactData, long environmentId) {
    var screenshots = artifactData.getScreenshots();
    var report = artifactData.getReport();
    var results = report.getResults();
    createSuitesAndTests(environmentId, results, screenshots);
  }

  private void createSuitesAndTests(
      long environmentId,
      List<MochaReportResultInternal> results,
      Map<String, byte[]> screenshots) {
    results.forEach(
        result -> {
          var file = result.getFile().replaceAll(START_PATH, "");
          processTestsWithoutSuite(environmentId, file, result.getTests(), screenshots);
          processSuites(environmentId, file, result.getSuites(), null, screenshots);
        });
  }

  private void processTestsWithoutSuite(
      long environmentId,
      String file,
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
                  configurationTestEntity ->
                      saveTest(mochaTest, configurationTestEntity, screenshots));
            }
          });
    }
  }

  private void processTests(
      long environmentId,
      String file,
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
                configurationTestEntity ->
                    saveTest(mochaTest, configurationTestEntity, screenshots));
          });
    }
  }

  private void processSuites(
      long environmentId,
      String file,
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
                  mochaSuite.getTests(),
                  configurationSuiteOptional.get(),
                  screenshots);
              processSuites(
                  environmentId,
                  file,
                  mochaSuite.getSuites(),
                  configurationSuiteOptional.get(),
                  screenshots);
            }
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

    //    if (screenshot != null) {
    TestScreenshotEntity.builder()
        .test(test)
        .filename(screenshotFilename.replace(SCREENSHOT_PATH, ""))
        .screenshot(screenshot)
        .build()
        .persist();
    //    } else {
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
