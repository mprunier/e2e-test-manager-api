package fr.plum.e2e.manager.core.domain.model.aggregate.metric.vo;

import java.util.UUID;

public record MetricsId(UUID value) {
  public static MetricsId generate() {
    return new MetricsId(UUID.randomUUID());
  }

  public static MetricsId fromUUID(UUID uuid) {
    return new MetricsId(uuid);
  }
}
