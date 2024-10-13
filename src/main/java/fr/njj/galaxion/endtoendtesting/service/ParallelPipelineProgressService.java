package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ParallelPipelineProgressService {

  private final PipelineRetrievalService pipelineRetrievalService;

  @Transactional
  public boolean isAllCompleted(String id) {
    var pipeline = pipelineRetrievalService.get(id);
    return pipeline.getParallelPipelineProgress().isAllCompleted();
  }

  @Transactional
  public void incrementCompletedPipelines(String id) {
    var pipeline = pipelineRetrievalService.get(id);
    pipeline.getParallelPipelineProgress().incrementCompletedPipelines();
  }
}
