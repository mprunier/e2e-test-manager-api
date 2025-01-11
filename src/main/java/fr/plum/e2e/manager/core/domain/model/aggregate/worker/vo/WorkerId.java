package fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo;

import java.util.UUID;

public record WorkerId(UUID value) {
  public static WorkerId generate() {
    return new WorkerId(UUID.randomUUID());
  }

  public static WorkerId fromUUID(UUID uuid) {
    return new WorkerId(uuid);
  }
}
