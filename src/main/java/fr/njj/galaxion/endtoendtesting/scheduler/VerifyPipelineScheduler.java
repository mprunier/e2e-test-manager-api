package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.service.CancelSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.CancelSuiteOrTestService;
import fr.njj.galaxion.endtoendtesting.service.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.PipelineService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RecordResultPipelineUseCase;
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

    @Getter
    @ConfigProperty(name = "gitlab.old-pipeline-to-verify-in-minutes")
    Integer oldPipelineToVerifyInMinutes;

    @Getter
    @ConfigProperty(name = "gitlab.old-pipeline-to-cancel-in-minutes")
    Integer oldPipelineToCancelInMinutes;

    private final AtomicBoolean inVerifyProgress = new AtomicBoolean(false);

    @Scheduled(every = "5m")
    @ActivateRequestContext
    public void execute() {
        if (inVerifyProgress.compareAndSet(false, true)) {
            try {
                var pipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToVerifyInMinutes);
                for (var pipeline : pipelines) {
                    var environment = pipeline.getEnvironment();
                    var gitlabJobResponse = gitlabService.getJob(environment.getToken(), environment.getProjectId(), pipeline.getId());
                    recordResultPipelineUseCase.execute(pipeline.getId(), gitlabJobResponse.getId(), GitlabJobStatus.fromHeaderValue(gitlabJobResponse.getStatus()));
                }

                var oldJPipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToCancelInMinutes);
                for (var pipeline : oldJPipelines) {
                    log.info("Pipeline id [{}] canceled.", pipeline.getId());
                    if (pipeline.getTestIds() != null) {
                        cancelSuiteOrTestService.cancel(pipeline.getId(), pipeline.getTestIds());
                    } else {
                        cancelSchedulerService.cancel(pipeline.getId());
                    }
                    pipelineService.cancel(pipeline.getId());
                }
            } finally {
                inVerifyProgress.set(false);
            }
        }
    }
}
