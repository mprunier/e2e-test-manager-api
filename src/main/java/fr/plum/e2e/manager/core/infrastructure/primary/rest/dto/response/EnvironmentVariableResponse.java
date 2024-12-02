package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnvironmentVariableResponse(
    @NotBlank String name,
    @NotBlank String displayValue,
    String description,
    @NotNull Boolean isHidden) {

  public static EnvironmentVariableResponse from(EnvironmentVariable environmentVariable) {
    return new EnvironmentVariableResponse(
        environmentVariable.getId().name(),
        environmentVariable.getDisplayValue().value(),
        environmentVariable.getDescription().value(),
        environmentVariable.getIsHidden().value());
  }
}
