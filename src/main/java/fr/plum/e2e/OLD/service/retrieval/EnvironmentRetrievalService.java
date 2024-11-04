package fr.plum.e2e.OLD.service.retrieval;

import static fr.plum.e2e.OLD.mapper.EnvironmentResponseMapper.buildEnvironmentResponse;

import fr.plum.e2e.OLD.domain.exception.EnvironmentNotFoundException;
import fr.plum.e2e.OLD.domain.response.EnvironmentResponse;
import fr.plum.e2e.OLD.model.entity.EnvironmentEntity;
import fr.plum.e2e.OLD.model.repository.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentRetrievalService {

  private final EnvironmentRepository environmentRepository;

  @Transactional
  public EnvironmentResponse getResponse(long id) {
    return buildEnvironmentResponse(get(id), true);
  }

  @Transactional
  public EnvironmentEntity get(long id) {
    return environmentRepository
        .findByIdOptional(id)
        .orElseThrow(() -> new EnvironmentNotFoundException(id));
  }

  @Transactional
  public List<EnvironmentEntity> getEnvironments() {
    return environmentRepository.findAll().stream().toList();
  }

  @Transactional
  public List<EnvironmentEntity> getEnvironmentsByBranchAndProjectId(
      String branch, String projectId) {
    return environmentRepository.findAllByBranchAndProjectId(branch, projectId);
  }

  @Transactional
  public List<EnvironmentEntity> getAllEnabled() {
    return environmentRepository.findAllEnvironmentsEnabled();
  }
}
