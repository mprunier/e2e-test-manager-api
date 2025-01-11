package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.TestResultVariableProjection;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TestResultVariableResponse(@NotBlank String name, @NotBlank String value) {

  public static TestResultVariableResponse fromTestResultVariableView(
      TestResultVariableProjection testResultVariableView) {
    return builder()
        .name(testResultVariableView.name())
        .value(testResultVariableView.value())
        .build();
  }
}
