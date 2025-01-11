package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationTestWithWorkerProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationTestWithWorkerResponse(
    @NotNull UUID id,
    @NotBlank String title,
    @NotNull ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt,
    List<WorkerResponse> workers) {

  public static ConfigurationTestWithWorkerResponse fromDomain(
      ConfigurationTestWithWorkerProjection domain) {
    return new ConfigurationTestWithWorkerResponse(
        domain.id(),
        domain.title(),
        domain.status(),
        domain.variables(),
        domain.tags(),
        domain.lastPlayedAt(),
        WorkerResponse.fromDomain(domain.workers()));
  }

  public static List<ConfigurationTestWithWorkerResponse> fromDomain(
      List<ConfigurationTestWithWorkerProjection> domains) {
    return domains.stream().map(ConfigurationTestWithWorkerResponse::fromDomain).toList();
  }
}
