// package fr.njj.galaxion.endtoendtesting.service;
//
// import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
// import fr.njj.galaxion.endtoendtesting.domain.event.RunCompletedEvent;
// import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
// import io.quarkus.cache.CacheManager;
// import jakarta.enterprise.context.ApplicationScoped;
// import jakarta.enterprise.event.Event;
// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @ApplicationScoped
// @RequiredArgsConstructor
// public class CompleteTestRunService {
//
//  private final PipelineRetrievalService pipelineRetrievalService;
//
//  private final Event<RunCompletedEvent> testRunCompletedEvent;
//
//  private final CacheManager cacheManager;
//
//  @Transactional
//  public void complete(String pipelineId) {
//
//    var pipeline = pipelineRetrievalService.get(pipelineId);
//
//    pipeline.setStatus(PipelineStatus.FINISH);
//
//    testRunCompletedEvent.fire(
//        RunCompletedEvent.builder().environmentId(pipeline.getEnvironment().getId()).build());
//
//    cacheManager
//        .getCache("in_progress_pipelines")
//        .ifPresent(cache -> cache.invalidateAll().await().indefinitely());
//  }
// }
