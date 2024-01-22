package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.SchedulerEntity;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelSchedulerService {

    private final SchedulerRetrievalService schedulerRetrievalService;
    private final GitlabService gitlabService;

    @Transactional
    public void cancel(String pipelineId) {
        var scheduler = schedulerRetrievalService.getSchedulerByPipelineId(pipelineId);
        cancel(scheduler);
    }

    @Transactional
    public void cancel(Long id) {
        var scheduler = schedulerRetrievalService.getScheduler(id);
        cancel(scheduler);
    }

    private void cancel(SchedulerEntity scheduler) {
        try {
            var environment = scheduler.getEnvironment();
            gitlabService.cancelPipeline(environment.getToken(), environment.getProjectId(), scheduler.getPipelineId());
            scheduler.setStatus(SchedulerStatus.CANCELED);
            scheduler.persist();
        } catch (Exception e) {
            scheduler.setStatus(SchedulerStatus.SYSTEM_ERROR);
            scheduler.persist();
        }
    }
}

