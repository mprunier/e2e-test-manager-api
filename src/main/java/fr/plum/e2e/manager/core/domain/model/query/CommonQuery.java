package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CommonQuery(EnvironmentId environmentId) {

  public static CommonQuery fromEnvironmentUUID(UUID environmentUUID) {
    return new CommonQuery(new EnvironmentId(environmentUUID));
  }
}
