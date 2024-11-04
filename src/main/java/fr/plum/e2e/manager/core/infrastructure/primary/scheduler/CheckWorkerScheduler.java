package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

public class CheckWorkerScheduler {}

// @Slf4j
// @ApplicationScoped
// @RequiredArgsConstructor
// public class VerifyPipelineScheduler {
//
//  private final CancelPipelineUseCase cancelPipelineUseCase;
//  private final PipelineRetrievalService pipelineRetrievalService;
//  private final RecordResultPipelineUseCase recordResultPipelineUseCase;
//  private final RetrieveGitlabJobService retrieveGitlabJobService;
//
//  @Getter
//  @ConfigProperty(name = "gitlab.old-pipeline-to-verify-in-minutes")
//  Integer oldPipelineToVerifyInMinutes;
//
//  @Getter
//  @ConfigProperty(name = "gitlab.old-pipeline-to-cancel-in-minutes")
//  Integer oldPipelineToCancelInMinutes;
//
//  private final AtomicBoolean inVerifyProgress = new AtomicBoolean(false);
//
//  @Scheduled(cron = "0 0/5 * * * ?")
//  @ActivateRequestContext
//  public void schedule() {
//    if (inVerifyProgress.compareAndSet(false, true)) {
//      log.trace("In progress pipelines verification scheduler started.");
//      try {
//        verifyPipeline();
//        cancelOldPipelines();
//      } catch (Exception e) {
//        log.error(
//            "Error during the verification of the in progress pipelines. : {}", e.getMessage());
//      } finally {
//        inVerifyProgress.set(false);
//      }
//    }
//  }
//
//  private void verifyPipeline() {
//    var pipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToVerifyInMinutes);
//    for (var pipeline : pipelines) {
//      log.debug("Verifying worker id [{}].", pipeline.getId());
//      var environment = pipeline.getEnvironment();
//      var gitlabJobResponse =
//          retrieveGitlabJobService.getJob(
//              environment.getToken(), environment.getProjectId(), pipeline.getId());
//      var status = gitlabJobResponse.getStatus().toWorkerStatus();
//      if (!WorkerStatus.IN_PROGRESS.equals(status)) {
//        //        recordResultPipelineUseCase.execute(pipeline.getId(), gitlabJobResponse.getId(),
//        // status);  TODO
//      }
//      log.debug("Pipeline id [{}] verified.", pipeline.getId());
//    }
//  }
//
//  private void cancelOldPipelines() {
//    var oldJPipelines = pipelineRetrievalService.getOldInProgress(oldPipelineToCancelInMinutes);
//    for (var pipeline : oldJPipelines) {
//      cancelPipelineUseCase.execute(pipeline.getId(), true);
//      log.info("Pipeline id [{}] canceled.", pipeline.getId());
//    }
//  }
// }
