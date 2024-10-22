package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.CompletePipelineService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CancelGitlabPipelineService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelPipelineUseCase {

  private final CompletePipelineService completePipelineService;
  private final CancelGitlabPipelineService cancelGitlabPipelineService;
  private final PipelineRetrievalService pipelineRetrievalService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(String pipelineId) {
    var pipeline = pipelineRetrievalService.get(pipelineId);
    try {
      var environment = pipeline.getEnvironment();
      cancelGitlabPipelineService.cancelPipeline(
          environment.getToken(), environment.getProjectId(), pipelineId);

      //      saveTestResult(pipeline, ConfigurationStatus.CANCELED, ReportPipelineStatus.CANCELED);
      completePipelineService.execute(pipelineId, ReportPipelineStatus.CANCELED);

    } catch (Exception e) {
      saveTestResult(pipeline, ConfigurationStatus.SYSTEM_ERROR, ReportPipelineStatus.SYSTEM_ERROR);
      completePipelineService.execute(pipelineId, ReportPipelineStatus.SYSTEM_ERROR);
    }
  }

  // We create a test result only for the run suite or test, not for with run all tests.
  private void saveTestResult(
      PipelineEntity pipeline,
      ConfigurationStatus status,
      ReportPipelineStatus reportPipelineStatus) {
    if (pipeline.getConfigurationTestIdsFilter() != null
        && !pipeline.getConfigurationTestIdsFilter().isEmpty()) {
      var configurationTestIdsToCancel =
          pipeline.getConfigurationTestIdsFilter().stream().map(Long::valueOf).toList();
      configurationTestIdsToCancel.forEach(
          configurationTestId -> {
            var configurationTestOptional =
                configurationTestRetrievalService.getOptional(configurationTestId);
            configurationTestOptional.ifPresent(
                configurationTestEntity ->
                    TestEntity.builder()
                        .configurationTest(configurationTestEntity)
                        .status(status)
                        .variables(pipeline.getVariables())
                        .createdBy(pipeline.getCreatedBy())
                        .errorMessage(reportPipelineStatus.getErrorMessage())
                        .build()
                        .persist());
          });
    }
  }
}
