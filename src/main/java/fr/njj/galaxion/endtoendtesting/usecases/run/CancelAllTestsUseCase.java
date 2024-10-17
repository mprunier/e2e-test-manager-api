package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.service.CompleteAllTestsRunService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CancelGitlabPipelineService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelAllTestsUseCase {

  private final CompleteAllTestsRunService completeAllTestsRunService;
  private final EnvironmentRetrievalService environmentRetrievalService;
  private final CancelGitlabPipelineService cancelGitlabPipelineService;

  @Transactional
  public void execute(long environmentId, String pipelineId) {
    try {
      var environment = environmentRetrievalService.get(environmentId);
      cancelGitlabPipelineService.cancelPipeline(
          environment.getToken(), environment.getProjectId(), pipelineId);
      completeAllTestsRunService.complete(pipelineId, ReportPipelineStatus.CANCELED);
    } catch (Exception e) {
      completeAllTestsRunService.complete(pipelineId, ReportPipelineStatus.SYSTEM_ERROR);
    }
  }
}
