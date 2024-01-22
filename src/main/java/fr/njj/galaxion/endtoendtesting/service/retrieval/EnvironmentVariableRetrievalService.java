package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentVariableRepository;
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
