package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.entity.PipelineGroupEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.PipelineGroupRepository;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineGroupService {

  private final PipelineRetrievalService pipelineRetrievalService;
  private final PipelineGroupRepository pipelineGroupRepository;

  @Transactional
  public PipelineGroupEntity get(String id) {
    var pipeline = pipelineRetrievalService.get(id);
    return pipeline.getPipelineGroup();
  }

  public PipelineGroupEntity getLastPipelineGroup(Long environmentId) {
    return pipelineGroupRepository.findLastPipelineGroupByEnvironmentI(environmentId);
  }
}
