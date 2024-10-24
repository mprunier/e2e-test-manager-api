package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.entity.PipelineGroupEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.PipelineGroupRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineGroupService {

  private final PipelineGroupRepository pipelineGroupRepository;

  public PipelineGroupEntity getLastPipelineGroup(Long environmentId) {
    return pipelineGroupRepository.findLastPipelineGroupByEnvironmentId(environmentId);
  }
}
