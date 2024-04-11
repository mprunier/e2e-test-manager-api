package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.usecases.run.AllTestsRunCompletedUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelSchedulerService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final GitlabService gitlabService;
    private final AllTestsRunCompletedUseCase allTestsRunCompletedUseCase;

    @Transactional
    public void cancel(long environmentId, String pipelineId) {
        try {
            var environment = environmentRetrievalService.getEnvironment(environmentId);
            gitlabService.cancelPipeline(environment.getToken(), environment.getProjectId(), pipelineId);
            allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.CANCELED);
        } catch (Exception e) {
            allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.SYSTEM_ERROR);
        }
    }
}

