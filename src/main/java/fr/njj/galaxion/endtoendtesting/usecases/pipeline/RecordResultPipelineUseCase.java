package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.CompleteAllTestsRunService;
import fr.njj.galaxion.endtoendtesting.service.CompleteTestRunService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobArtifactsService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.TestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RecordResultPipelineUseCase {

  private final GenerateTestReportUseCase generateTestReportUseCase;
  private final GenerateAllTestsReportUseCase generateAllTestsReportUseCase;
  private final CompleteAllTestsRunService completeAllTestsRunService;
  private final CompleteTestRunService completeTestRunService;

  private final PipelineRetrievalService pipelineRetrievalService;
  private final TestRetrievalService testRetrievalService;
  private final RetrieveGitlabJobArtifactsService retrieveGitlabJobArtifactsService;

  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;

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
          generateAllTestsReportUseCase.execute(artifactData, environmentId);
          completeAllTestsRunService.complete(pipeline.getId(), null);
        } else {
          completeAllTestsRunService.complete(
              pipeline.getId(), ReportPipelineStatus.NO_REPORT_ERROR);
        }
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        completeAllTestsRunService.complete(pipeline.getId(), ReportPipelineStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error(e.getMessage());
      completeAllTestsRunService.complete(pipeline.getId(), ReportPipelineStatus.SYSTEM_ERROR);
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
      completeTestRunService.complete(pipeline.getId());
    }
  }
}
