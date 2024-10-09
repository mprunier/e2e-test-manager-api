package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.SCREENSHOT_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestScreenshotEntity;
import fr.njj.galaxion.endtoendtesting.service.CompletePipelineService;
import fr.njj.galaxion.endtoendtesting.service.SaveCancelResultTestService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobArtifactsService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.SearchSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
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
  private final SaveCancelResultTestService saveCancelResultTestService;
  private final PipelineRetrievalService pipelineRetrievalService;
  private final RetrieveGitlabJobArtifactsService retrieveGitlabJobArtifactsService;

  @Transactional
  public void execute(String pipelineId, String jobId, GitlabJobStatus status) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
    if (!PipelineStatus.IN_PROGRESS.equals(pipeline.getStatus())) {
      return;
    }
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
          saveTestResult(
              artifactData, environmentId, pipeline, pipeline.getConfigurationTestIdsFilter());
          completePipelineService.execute(
              pipeline.getId(),
              GitlabJobStatus.success.equals(status)
                  ? PipelineStatus.SUCCESS
                  : PipelineStatus.FAILED);
        } else {
          completePipelineService.execute(pipeline.getId(), PipelineStatus.NO_REPORT_ERROR);
        }
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        saveCancelResultTestService.saveTestResult(
            pipeline,
            ConfigurationStatus.CANCELED,
            PipelineStatus.CANCELED.getErrorMessage(),
            false);
        completePipelineService.execute(pipeline.getId(), PipelineStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error("Error while recording pipeline result.", e);
      completePipelineService.execute(pipeline.getId(), PipelineStatus.SYSTEM_ERROR);
    }
  }

  @Transactional
  public void saveTestResult(
      ArtifactDataInternal artifactData,
      long environmentId,
      PipelineEntity pipeline,
      List<String> configurationTestIdsFilter) {
    var screenshots = artifactData.getScreenshots();
    var report = artifactData.getReport();
    var results = report.getResults();
    results.forEach(
        result -> {
          var file = result.getFile().replaceAll(START_PATH, "");
          processTestsWithoutSuite(
              environmentId,
              pipeline,
              file,
              configurationTestIdsFilter,
              result.getTests(),
              screenshots);
          processSuites(
              environmentId,
              pipeline,
              file,
              configurationTestIdsFilter,
              result.getSuites(),
              null,
              screenshots);
        });
  }

  private void processTestsWithoutSuite(
      long environmentId,
      PipelineEntity pipeline,
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
                      saveTest(pipeline, mochaTest, configurationTestEntity, screenshots);
                    }
                  });
            }
          });
    }
  }

  private void processSuites(
      long environmentId,
      PipelineEntity pipeline,
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
                  pipeline,
                  file,
                  configurationTestIdsFilter,
                  mochaSuite.getTests(),
                  configurationSuiteOptional.get(),
                  screenshots);
              processSuites(
                  environmentId,
                  pipeline,
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
      PipelineEntity pipeline,
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
                    saveTest(pipeline, mochaTest, configurationTestEntity, screenshots);
                  }
                });
          });
    }
  }

  private void saveTest(
      PipelineEntity pipeline,
      MochaReportTestInternal mochaTest,
      ConfigurationTestEntity configurationTest,
      Map<String, byte[]> screenshots) {

    var status = getConfigurationStatus(mochaTest);
    var test =
        TestEntity.builder()
            .pipelineId(pipeline.getId())
            .configurationTest(configurationTest)
            .status(status)
            .errorMessage(mochaTest.getErr() != null ? mochaTest.getErr().getMessage() : null)
            .errorStacktrace(mochaTest.getErr() != null ? mochaTest.getErr().getEstack() : null)
            .code(mochaTest.getCode())
            .duration(mochaTest.getDuration())
            .createdBy(pipeline.getCreatedBy())
            .createdAt(ZonedDateTime.now())
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
  }
}
