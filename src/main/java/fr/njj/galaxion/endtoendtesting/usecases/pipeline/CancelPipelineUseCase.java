package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelPipelineUseCase {

  private final PipelineRetrievalService pipelineRetrievalService;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(String id) {
    pipelineRetrievalService.get(id).setStatus(PipelineStatus.CANCELED);

    cacheManager
        .getCache("in_progress_pipelines")
        .ifPresent(cache -> cache.invalidateAll().await().indefinitely());
  }
}
