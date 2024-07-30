package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.CancelPipelineUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RecordResultPipelineUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.run.CancelAllTestsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.run.CancelTestUseCase;
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

    private final CancelTestUseCase cancelTestUseCase;
    private final CancelAllTestsUseCase cancelAllTestsUseCase;
    private final PipelineRetrievalService pipelineRetrievalService;
    private final CancelPipelineUseCase cancelPipelineUseCase;
    private final RecordResultPipelineUseCase recordResultPipelineUseCase;
    private final RetrieveGitlabJobService retrieveGitlabJobService;

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
                    var environment = pipeline.getEnvironment();
                    var gitlabJobResponse = retrieveGitlabJobService.getJob(environment.getToken(), environment.getProjectId(), pipeline.getId());
                    var status = GitlabJobStatus.fromHeaderValue(gitlabJobResponse.getStatus());
                    if (!GitlabJobStatus.created.equals(status) && !GitlabJobStatus.pending.equals(status) && !GitlabJobStatus.running.equals(status)) {
                        recordResultPipelineUseCase.execute(pipeline.getId(), gitlabJobResponse.getId(), status);
                    }
                    log.info("Pipeline id [{}] verified.", pipeline.getId());
                }

                var oldJPipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToCancelInMinutes);
                for (var pipeline : oldJPipelines) {
                    if (pipeline.getTestIds() != null) {
                        cancelTestUseCase.execute(pipeline.getId(), pipeline.getTestIds());
                    } else {
                        cancelAllTestsUseCase.execute(pipeline.getEnvironment().getId(), pipeline.getId());
                    }
                    cancelPipelineUseCase.execute(pipeline.getId());
                    log.info("Pipeline id [{}] canceled.", pipeline.getId());
                }
            } catch (Exception e) {
                log.error("Error during the verification of the in progress pipelines. : {}", e.getMessage());
            } finally {
                inVerifyProgress.set(false);
            }
        }
    }
}

