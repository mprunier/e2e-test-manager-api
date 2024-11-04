package fr.plum.e2e.OLD.usecases.pipeline;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import fr.plum.e2e.OLD.service.CompletePipelineService;
import fr.plum.e2e.OLD.service.SaveCancelResultTestService;
import fr.plum.e2e.OLD.service.gitlab.CancelGitlabPipelineService;
import fr.plum.e2e.OLD.service.retrieval.PipelineRetrievalService;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.WorkerStatus;
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
  private final SaveCancelResultTestService saveCancelResultTestService;

  @Transactional
  public void execute(String pipelineId, boolean isSaveTestResult) {
    var pipeline = pipelineRetrievalService.get(pipelineId);
    try {
      var environment = pipeline.getEnvironment();
      cancelGitlabPipelineService.cancelPipeline(
          environment.getToken(), environment.getProjectId(), pipelineId);

      if (isSaveTestResult) {
        saveCancelResultTestService.saveTestResult(
            pipeline, ConfigurationStatus.CANCELED, WorkerStatus.CANCELED.getErrorMessage(), true);
      }
      completePipelineService.execute(pipelineId, WorkerStatus.CANCELED);

    } catch (Exception e) {
      saveCancelResultTestService.saveTestResult(
          pipeline,
          ConfigurationStatus.SYSTEM_ERROR,
          WorkerStatus.SYSTEM_ERROR.getErrorMessage(),
          false);
      completePipelineService.execute(pipelineId, WorkerStatus.SYSTEM_ERROR);
    }
  }
}
