package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RetrieveGitlabJobService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.CancelPipelineUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RecordResultPipelineUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class VerifyPipelineScheduler {

  //  private final CancelTestUseCase cancelTestUseCase;
  private final CancelPipelineUseCase cancelPipelineUseCase;
  private final PipelineRetrievalService pipelineRetrievalService;
  //  private final CancelPipelineUseCase cancelPipelineUseCase;
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
      log.trace("In progress pipelines verification scheduler started.");
      try {
        verifyPipeline();
        cancelOldPipelines();
      } catch (Exception e) {
        log.error(
            "Error during the verification of the in progress pipelines. : {}", e.getMessage());
      } finally {
        inVerifyProgress.set(false);
      }
    }
  }

  private void verifyPipeline() {
    var pipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToVerifyInMinutes);
    for (var pipeline : pipelines) {
      var environment = pipeline.getEnvironment();
      var gitlabJobResponse =
          retrieveGitlabJobService.getJob(
              environment.getToken(), environment.getProjectId(), pipeline.getId());
      var status = GitlabJobStatus.fromHeaderValue(gitlabJobResponse.getStatus());
      if (!GitlabJobStatus.created.equals(status)
          && !GitlabJobStatus.pending.equals(status)
          && !GitlabJobStatus.running.equals(status)) {
        recordResultPipelineUseCase.execute(pipeline.getId(), gitlabJobResponse.getId(), status);
      }
      log.info("Pipeline id [{}] verified.", pipeline.getId());
    }
  }

  private void cancelOldPipelines() {
    var oldJPipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToCancelInMinutes);
    for (var pipeline : oldJPipelines) {
      cancelPipelineUseCase.execute(pipeline.getId(), true);
      log.info("Pipeline id [{}] canceled.", pipeline.getId());
    }
  }
}
