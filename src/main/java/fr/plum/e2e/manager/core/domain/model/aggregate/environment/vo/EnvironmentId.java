package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.util.UUID;

public record EnvironmentId(UUID value) {

  public EnvironmentId {
    Assert.notNull("EnvironmentId value", value);
  }

  public static EnvironmentId generate() {
    return new EnvironmentId(UUID.randomUUID());
  }
}
