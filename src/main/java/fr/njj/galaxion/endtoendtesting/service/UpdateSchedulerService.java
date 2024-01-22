package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateSchedulerService {

    private final GitlabService gitlabService;
    private final ReportSchedulerService reportSchedulerService;
    private final SchedulerRetrievalService schedulerRetrievalService;

    @Transactional
    public boolean update(String pipelineId) {

        var scheduler = schedulerRetrievalService.getSchedulerByPipelineId(pipelineId);
        try {
            var environment = scheduler.getEnvironment();
            var gitlabJobLogsResponse = gitlabService.getJob(environment.getToken(), environment.getProjectId(), pipelineId);

            if (GitlabJobStatus.success.name().equals(gitlabJobLogsResponse.getStatus()) ||
                GitlabJobStatus.failed.name().equals(gitlabJobLogsResponse.getStatus())) {
                log.info("Update Scheduler Tests on Job id [{}]. Status is [{}].", pipelineId, gitlabJobLogsResponse.getStatus());
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), gitlabJobLogsResponse.getId());
                if (artifactData.getReport() != null) {
                    reportSchedulerService.report(artifactData, scheduler);
                } else {
                    scheduler.setStatus(SchedulerStatus.NO_REPORT_ERROR);
                }
                return true;

            } else if (GitlabJobStatus.canceled.name().equals(gitlabJobLogsResponse.getStatus()) ||
                       GitlabJobStatus.skipped.name().equals(gitlabJobLogsResponse.getStatus())) {
                log.info("Update Scheduler Tests on Job id [{}]. Status is [{}].", pipelineId, gitlabJobLogsResponse.getStatus());
                scheduler.setStatus(SchedulerStatus.CANCELED);
                return true;

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            scheduler.setStatus(SchedulerStatus.SYSTEM_ERROR);
            return true;
        }
        return false;
    }
}

