package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteWithWorkerProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationSuiteWithWorkerResponse(
    @NotNull UUID id,
    @NotBlank String title,
    @NotBlank String file,
    @NotNull ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    @NotEmpty List<ConfigurationTestWithWorkerResponse> tests,
    ZonedDateTime lastPlayedAt,
    @NotNull Boolean hasNewTest,
    String group,
    List<WorkerResponse> workers) {

  public static ConfigurationSuiteWithWorkerResponse fromDomain(
      ConfigurationSuiteWithWorkerProjection domain) {
    return new ConfigurationSuiteWithWorkerResponse(
        domain.id(),
        domain.title(),
        domain.file(),
        domain.status(),
        domain.variables(),
        domain.tags(),
        ConfigurationTestWithWorkerResponse.fromDomain(domain.tests()),
        domain.lastPlayedAt(),
        domain.hasNewTest(),
        domain.group(),
        WorkerResponse.fromDomain(domain.workers()));
  }
}
