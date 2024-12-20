package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsVariableProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnvironmentVariableResponse(
    @NotBlank String name, @NotBlank String value, String description, @NotNull Boolean isHidden) {

  public static EnvironmentVariableResponse from(
      EnvironmentDetailsVariableProjection environmentVariable) {
    return new EnvironmentVariableResponse(
        environmentVariable.name(),
        environmentVariable.getDisplayValue(),
        environmentVariable.description(),
        environmentVariable.isHidden());
  }
}
