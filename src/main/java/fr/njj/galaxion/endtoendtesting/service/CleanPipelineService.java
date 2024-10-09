package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanPipelineService {

  private final PipelineRetrievalService pipelineRetrievalService;

  @Transactional
  public void getLastPipelineGroup(String pipelineId) {
    var pipelineGroup = pipelineRetrievalService.getGroup(pipelineId);
    if (pipelineGroup != null) {
      pipelineGroup.delete();
    } else {
      var pipeline = pipelineRetrievalService.get(pipelineId);
      pipeline.delete();
    }
  }
}
