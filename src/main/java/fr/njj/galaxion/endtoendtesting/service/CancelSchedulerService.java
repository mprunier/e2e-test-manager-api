package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.usecases.scheduler.UpdateSchedulerStatusUseCase;
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
    private final UpdateSchedulerStatusUseCase updateSchedulerStatusUseCase;

    @Transactional
    public void cancel(long environmentId, String pipelineId) {
        try {
            var environment = environmentRetrievalService.getEnvironment(environmentId);
            gitlabService.cancelPipeline(environment.getToken(), environment.getProjectId(), pipelineId);
            updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.CANCELED);
        } catch (Exception e) {
            updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.SYSTEM_ERROR);
        }
    }
}

