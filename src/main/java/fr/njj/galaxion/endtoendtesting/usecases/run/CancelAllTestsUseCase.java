package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
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

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CancelGitlabPipelineService cancelGitlabPipelineService;
    private final AllTestsRunCompletedUseCase allTestsRunCompletedUseCase;

    @Transactional
    public void execute(long environmentId, String pipelineId) {
        try {
            var environment = environmentRetrievalService.getEnvironment(environmentId);
            cancelGitlabPipelineService.cancelPipeline(environment.getToken(), environment.getProjectId(), pipelineId);
            allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.CANCELED);
        } catch (Exception e) {
            allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.SYSTEM_ERROR);
        }
    }
}

