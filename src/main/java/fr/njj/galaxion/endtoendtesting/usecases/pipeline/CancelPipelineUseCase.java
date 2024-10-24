package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.service.CompletePipelineService;
import fr.njj.galaxion.endtoendtesting.service.SaveCancelResultTestService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CancelGitlabPipelineService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
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
            pipeline, ConfigurationStatus.CANCELED, PipelineStatus.CANCELED.getErrorMessage());
      }
      completePipelineService.execute(pipelineId, PipelineStatus.CANCELED);

    } catch (Exception e) {
      saveCancelResultTestService.saveTestResult(
          pipeline,
          ConfigurationStatus.SYSTEM_ERROR,
          PipelineStatus.SYSTEM_ERROR.getErrorMessage());
      completePipelineService.execute(pipelineId, PipelineStatus.SYSTEM_ERROR);
    }
  }
}
