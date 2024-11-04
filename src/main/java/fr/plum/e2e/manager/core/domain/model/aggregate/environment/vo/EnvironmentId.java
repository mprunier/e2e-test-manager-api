package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import java.util.UUID;

public record EnvironmentId(UUID value) {
  public static EnvironmentId generate() {
    return new EnvironmentId(UUID.randomUUID());
  }

  public static EnvironmentId fromUUID(UUID uuid) {
    return new EnvironmentId(uuid);
  }
}
