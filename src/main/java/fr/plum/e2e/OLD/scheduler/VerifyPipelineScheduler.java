// package fr.njj.galaxion.endtoendtesting.scheduler;
//
// import fr.njj.galaxion.endtoendtesting.aaaaa.application.event.worker.WorkerStatus;
// import gitlab.mapper.fr.plum.e2e.manager.RetrieveGitlabJobService;
// import retrieval.mapper.fr.plum.e2e.manager.PipelineRetrievalService;
// import pipeline.usecases.fr.plum.e2e.manager.CancelPipelineUseCase;
// import pipeline.usecases.fr.plum.e2e.manager.RecordResultPipelineUseCase;
// import io.quarkus.scheduler.Scheduled;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.enterprise.context.control.ActivateRequestContext;
// import java.util.concurrent.atomic.AtomicBoolean;
// import lombok.Getter;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.eclipse.microprofile.config.inject.ConfigProperty;
//
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
//  @ConfigProperty(name = "business.scheduler.worker.report.verification.interval-minutes")
//  Integer oldPipelineToVerifyInMinutes;
//
//  @Getter
//  @ConfigProperty(name = "business.scheduler.worker.report.cancel-timeout.interval-minutes")
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
