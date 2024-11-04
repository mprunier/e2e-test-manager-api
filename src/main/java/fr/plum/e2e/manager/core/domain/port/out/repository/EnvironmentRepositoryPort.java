package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import java.util.List;
import java.util.Optional;

public interface EnvironmentRepositoryPort {
  Optional<Environment> findById(EnvironmentId environmentId);

  List<Environment> findAllByProjectIdAndBranch(String projectId, String branch);

  void save(Environment environment);

  void update(Environment environment);

  boolean existsByDescription(EnvironmentDescription description);
}
