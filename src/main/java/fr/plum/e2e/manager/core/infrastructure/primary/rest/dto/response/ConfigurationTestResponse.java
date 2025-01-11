package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationTestProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationTestResponse(
    @NotNull UUID id,
    @NotBlank String title,
    @NotNull ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt) {
  public static ConfigurationTestResponse fromDomain(ConfigurationTestProjection domain) {
    return new ConfigurationTestResponse(
        domain.id(),
        domain.title(),
        domain.status(),
        domain.variables(),
        domain.tags(),
        domain.lastPlayedAt());
  }

  public static List<ConfigurationTestResponse> fromDomain(
      List<ConfigurationTestProjection> domains) {
    return domains.stream().map(ConfigurationTestResponse::fromDomain).toList();
  }
}
