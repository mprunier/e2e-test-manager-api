package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobType;
import fr.njj.galaxion.endtoendtesting.service.CancelSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.CancelSuiteOrTestService;
import fr.njj.galaxion.endtoendtesting.service.JobRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.JobService;
import fr.njj.galaxion.endtoendtesting.service.UpdateSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.UpdateSuiteOrTestService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateScheduler {

    private final CancelSuiteOrTestService cancelSuiteOrTestService;
    private final CancelSchedulerService cancelSchedulerService;
    private final UpdateSuiteOrTestService updateSuiteOrTestService;
    private final UpdateSchedulerService updateSchedulerService;
    private final JobRetrievalService jobRetrievalService;
    private final JobService jobService;

    @Getter
    @ConfigProperty(name = "gitlab.test.job.canceled-older-than-minutes")
    Integer oldTestJobMinutes;

    @Getter
    @ConfigProperty(name = "gitlab.suite.job.canceled-older-than-minutes")
    Integer oldSuiteJobMinutes;

    @Getter
    @ConfigProperty(name = "gitlab.scheduler.job.canceled-older-than-minutes")
    Integer oldSchedulerJobMinutes;

    private final AtomicBoolean inTestProgress = new AtomicBoolean(false);
    private final AtomicBoolean inSuiteProgress = new AtomicBoolean(false);
    private final AtomicBoolean inSchedulerProgress = new AtomicBoolean(false);

    @Scheduled(every = "5s")
    @ActivateRequestContext
    public void updateTestScheduler() {
        if (inTestProgress.compareAndSet(false, true)) {
            try {
                var jobs = jobRetrievalService.getInProgress(JobType.TEST);
                for (var job : jobs) {
                    var isFinish = updateSuiteOrTestService.update(job.getPipelineId(), job.getTestIds());
                    if (isFinish) {
                        jobService.finish(job.getId());
                    }
                }

                var oldJobs = jobRetrievalService.getOldInProgress(JobType.TEST, oldTestJobMinutes);
                for (var job : oldJobs) {
                    log.info("Job id [{}] canceled.", job.getPipelineId());
                    cancelSuiteOrTestService.cancel(job.getPipelineId(), job.getTestIds());
                    jobService.cancel(job.getId());
                }
            } finally {
                inTestProgress.set(false);
            }
        }
    }

    @Scheduled(every = "10s")
    @ActivateRequestContext
    public void updateSuiteScheduler() {
        if (inSuiteProgress.compareAndSet(false, true)) {
            try {
                var jobs = jobRetrievalService.getInProgress(JobType.SUITE);
                for (var job : jobs) {
                    var isFinish = updateSuiteOrTestService.update(job.getPipelineId(), job.getTestIds());
                    if (isFinish) {
                        jobService.finish(job.getId());
                    }
                }

                var oldJobs = jobRetrievalService.getOldInProgress(JobType.SUITE, oldSuiteJobMinutes);
                for (var job : oldJobs) {
                    log.info("Job id [{}] canceled.", job.getPipelineId());
                    cancelSuiteOrTestService.cancel(job.getPipelineId(), job.getTestIds());
                    jobService.cancel(job.getId());
                }
            } finally {
                inSuiteProgress.set(false);
            }
        }
    }

    @Scheduled(every = "30s")
    @ActivateRequestContext
    public void updateAllTestScheduler() {
        if (inSchedulerProgress.compareAndSet(false, true)) {
            try {
                var jobs = jobRetrievalService.getInProgress(JobType.ALL_TESTS);
                for (var job : jobs) {
                    var isFinish = updateSchedulerService.update(job.getPipelineId());
                    if (isFinish) {
                        jobService.finish(job.getId());
                    }
                }

                var oldJobs = jobRetrievalService.getOldInProgress(JobType.ALL_TESTS, oldSchedulerJobMinutes);
                for (var job : oldJobs) {
                    log.info("Job id [{}] canceled.", job.getPipelineId());
                    cancelSchedulerService.cancel(job.getPipelineId());
                    jobService.cancel(job.getId());
                }
            } finally {
                inSchedulerProgress.set(false);
            }
        }
    }
}
