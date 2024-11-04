package fr.plum.e2e.OLD.service;

import fr.plum.e2e.OLD.model.entity.PipelineGroupEntity;
import fr.plum.e2e.OLD.model.repository.PipelineGroupRepository;
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
