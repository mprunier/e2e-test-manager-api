package fr.plum.e2e.manager.core.domain.port.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsProjection;

public interface GetEnvironmentDetailsPort {
  EnvironmentDetailsProjection find(EnvironmentId environmentId);
}
