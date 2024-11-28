package fr.plum.e2e.manager.core.domain.service;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.exception.DuplicateEnvironmentException;
import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnvironmentService {

  private final EnvironmentRepositoryPort environmentRepositoryPort;

  public Environment getEnvironment(EnvironmentId id) {
    return environmentRepositoryPort
        .find(id)
        .orElseThrow(() -> new EnvironmentNotFoundException(id));
  }

  public void assertEnvironmentDescriptionNotExist(EnvironmentDescription description) {
    if (environmentRepositoryPort.exist(description)) {
      throw new DuplicateEnvironmentException(description);
    }
  }
}
