package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsVariableView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnvironmentVariableResponse(
    @NotBlank String name, @NotBlank String value, String description, @NotNull Boolean isHidden) {

  public static EnvironmentVariableResponse from(
      EnvironmentDetailsVariableView environmentVariable) {
    return new EnvironmentVariableResponse(
        environmentVariable.name(),
        environmentVariable.value(),
        environmentVariable.description(),
        environmentVariable.isHidden());
  }
}
