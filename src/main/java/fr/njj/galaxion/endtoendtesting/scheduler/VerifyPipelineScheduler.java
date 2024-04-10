package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.service.CancelSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.CancelSuiteOrTestService;
import fr.njj.galaxion.endtoendtesting.service.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.PipelineService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RecordResultPipelineUseCase;
import fr.njj.galaxion.endtoendtesting.websocket.events.UpdateFinalMetricsEventService;
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
public class VerifyPipelineScheduler {

    private final CancelSuiteOrTestService cancelSuiteOrTestService;
    private final CancelSchedulerService cancelSchedulerService;
    private final PipelineRetrievalService pipelineRetrievalService;
    private final PipelineService pipelineService;
    private final RecordResultPipelineUseCase recordResultPipelineUseCase;
    private final GitlabService gitlabService;
    private final UpdateFinalMetricsEventService updateFinalMetricsEventService;
    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;

    @Getter
    @ConfigProperty(name = "gitlab.old-pipeline-to-verify-in-minutes")
    Integer oldPipelineToVerifyInMinutes;

    @Getter
    @ConfigProperty(name = "gitlab.old-pipeline-to-cancel-in-minutes")
    Integer oldPipelineToCancelInMinutes;

    private final AtomicBoolean inVerifyProgress = new AtomicBoolean(false);

    @Scheduled(every = "5m")
    @ActivateRequestContext
    public void schedule() {
        if (inVerifyProgress.compareAndSet(false, true)) {
            try {
                var pipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToVerifyInMinutes);
                for (var pipeline : pipelines) {
                    log.info("Pipeline id [{}] verified.", pipeline.getId());
                    var environment = pipeline.getEnvironment();
                    var gitlabJobResponse = gitlabService.getJob(environment.getToken(), environment.getProjectId(), pipeline.getId());
                    var status = GitlabJobStatus.fromHeaderValue(gitlabJobResponse.getStatus());
                    if (!GitlabJobStatus.created.equals(status) && !GitlabJobStatus.pending.equals(status) && !GitlabJobStatus.running.equals(status)) {
                        recordResultPipelineUseCase.execute(pipeline.getId(), gitlabJobResponse.getId(), status);
                        buildAndSendFinalMetricsEvent(environment.getId());
                    }
                }

                var oldJPipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToCancelInMinutes);
                for (var pipeline : oldJPipelines) {
                    log.info("Pipeline id [{}] canceled.", pipeline.getId());
                    if (pipeline.getTestIds() != null) {
                        cancelSuiteOrTestService.cancel(pipeline.getId(), pipeline.getTestIds());
                    } else {
                        cancelSchedulerService.cancel(pipeline.getEnvironment().getId(), pipeline.getId());
                    }
                    pipelineService.cancel(pipeline.getId());
                }
            } catch (Exception e) {
                log.error("Error during the verification of the in progress pipelines. : {}", e.getMessage());
            } finally {
                inVerifyProgress.set(false);
            }
        }
    }

    private void buildAndSendFinalMetricsEvent(long environmentId) {
        var finalMetrics = calculateFinalMetricsUseCase.execute(environmentId);
        updateFinalMetricsEventService.send(environmentId, finalMetrics);
    }
}

