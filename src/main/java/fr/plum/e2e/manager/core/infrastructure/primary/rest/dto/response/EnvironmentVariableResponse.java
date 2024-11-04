package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;

public record EnvironmentVariableResponse(
    String name, String displayValue, String description, Boolean isHidden) {

  public static EnvironmentVariableResponse from(EnvironmentVariable environmentVariable) {
    return new EnvironmentVariableResponse(
        environmentVariable.getId().name(),
        environmentVariable.getDisplayValue().value(),
        environmentVariable.getDescription().value(),
        environmentVariable.getIsHidden().value());
  }
}
