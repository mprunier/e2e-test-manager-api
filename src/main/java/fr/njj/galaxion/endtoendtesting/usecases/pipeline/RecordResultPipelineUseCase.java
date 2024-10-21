package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.service.CompleteRunService;
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

  private final GenerateReportUseCase generateReportUseCase;
  private final CompleteRunService completeRunService;

  private final PipelineRetrievalService pipelineRetrievalService;
  private final TestRetrievalService testRetrievalService;
  private final RetrieveGitlabJobArtifactsService retrieveGitlabJobArtifactsService;

  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;

  @Transactional
  public void execute(String pipelineId, String jobId, GitlabJobStatus status) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
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
          generateReportUseCase.execute(
              artifactData, environmentId, pipeline.getConfigurationTestIdsFilter());
          completeRunService.execute(pipeline.getId(), ReportPipelineStatus.FINISH);
        } else {
          completeRunService.execute(pipeline.getId(), ReportPipelineStatus.NO_REPORT_ERROR);
        }
      } else if (GitlabJobStatus.canceled.equals(status)
          || GitlabJobStatus.skipped.equals(status)) {
        completeRunService.execute(pipeline.getId(), ReportPipelineStatus.CANCELED);
      }
    } catch (Exception e) {
      log.error("Error while recording pipeline result.", e);
      completeRunService.execute(pipeline.getId(), ReportPipelineStatus.SYSTEM_ERROR);
    } finally {
      updateFinalMetricsEvent.fire(
          UpdateFinalMetricsEvent.builder().environmentId(environment.getId()).build());
    }
  }

  //  private void globalUpdate(
  //      PipelineEntity pipeline,
  //      GitlabJobStatus status,
  //      EnvironmentEntity environment,
  //      String jobId) {
  //
  //  }

  //  private void partialUpdate(
  //      String jobId,
  //      GitlabJobStatus status,
  //      PipelineEntity pipeline,
  //      EnvironmentEntity environment) {
  //    var testIds = pipeline.getConfigurationTestIdsFilter().stream().map(Long::valueOf).toList();
  //    var tests = testRetrievalService.getAll(testIds);
  //
  //    try {
  //      if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
  //        var artifactData =
  //            retrieveGitlabJobArtifactsService.getArtifactData(
  //                environment.getToken(), environment.getProjectId(), jobId);
  //        generateTestReportUseCase.execute(artifactData, tests);
  //      } else if (GitlabJobStatus.canceled.equals(status)
  //          || GitlabJobStatus.skipped.equals(status)) {
  //        updateStatus(tests, ConfigurationStatus.CANCELED);
  //      }
  //    } catch (Exception e) {
  //      log.error(e.getMessage());
  //      updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR);
  //    } finally {
  //      completeTestRunService.complete(pipeline.getId());
  //    }
  //  }
}
