package fr.plum.e2e.OLD.service;

import fr.plum.e2e.OLD.domain.event.internal.PipelineCompletedEvent;
import fr.plum.e2e.OLD.service.retrieval.PipelineRetrievalService;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.enumeration.WorkerStatus;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CompletePipelineService {

  private final PipelineRetrievalService pipelineRetrievalService;

  private final Event<PipelineCompletedEvent> pipelineCompletedEvent;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(String pipelineId, WorkerStatus workerStatus) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
    var environmentId = pipeline.getEnvironment().getId();

    pipeline.setStatus(workerStatus);

    cacheManager
        .getCache("in_progress_pipelines")
        .ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());

    pipelineCompletedEvent.fire(
        PipelineCompletedEvent.builder()
            .environmentId(environmentId)
            .pipelineId(pipelineId)
            .type(pipeline.getType())
            //            .configurationTestIdsFilter(pipeline.getConfigurationTestIdsFilter())
            .build());
  }
}
