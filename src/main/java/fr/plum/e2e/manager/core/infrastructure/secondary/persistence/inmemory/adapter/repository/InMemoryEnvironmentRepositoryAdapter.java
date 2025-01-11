package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryEnvironmentRepositoryAdapter implements EnvironmentRepositoryPort {
  private final Map<EnvironmentId, Environment> environments = new HashMap<>();

  @Override
  public Optional<Environment> find(EnvironmentId environmentId) {
    return Optional.ofNullable(environments.get(environmentId));
  }

  @Override
  public List<Environment> findAll(String projectId, String branch) {
    return environments.values().stream()
        .filter(env -> env.getSourceCodeInformation().projectId().equals(projectId))
        .filter(env -> env.getSourceCodeInformation().branch().equals(branch))
        .collect(Collectors.toList());
  }

  @Override
  public void save(Environment environment) {
    environments.put(environment.getId(), environment);
  }

  @Override
  public void update(Environment environment) {
    environments.put(environment.getId(), environment);
  }

  @Override
  public boolean exist(EnvironmentDescription description) {
    return environments.values().stream()
        .anyMatch(env -> env.getEnvironmentDescription().equals(description));
  }
}
