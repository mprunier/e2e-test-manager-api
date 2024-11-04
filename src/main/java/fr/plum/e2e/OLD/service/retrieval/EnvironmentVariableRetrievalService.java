package fr.plum.e2e.OLD.service.retrieval;

import fr.plum.e2e.OLD.model.entity.EnvironmentVariableEntity;
import fr.plum.e2e.OLD.model.repository.EnvironmentVariableRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentVariableRetrievalService {

  private final EnvironmentVariableRepository environmentVariableRepository;

  @Transactional
  public Optional<EnvironmentVariableEntity> getByEnvironmentAndName(
      long environmentId, String name) {
    return environmentVariableRepository.findByEnvironmentAndName(environmentId, name);
  }
}
