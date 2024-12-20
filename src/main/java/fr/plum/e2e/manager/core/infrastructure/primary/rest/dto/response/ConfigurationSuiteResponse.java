package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ConfigurationSuiteResponse(
    @NotNull UUID id,
    @NotBlank String title,
    @NotBlank String file,
    @NotNull ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    @NotEmpty List<ConfigurationTestResponse> tests,
    ZonedDateTime lastPlayedAt,
    @NotNull Boolean hasNewTest,
    String group) {

  public static ConfigurationSuiteResponse fromDomain(ConfigurationSuiteView domain) {
    return new ConfigurationSuiteResponse(
        domain.id(),
        domain.title(),
        domain.file(),
        domain.status(),
        domain.variables(),
        domain.tags(),
        ConfigurationTestResponse.fromDomain(domain.tests()),
        domain.lastPlayedAt(),
        domain.hasNewTest(),
        domain.group());
  }
}
