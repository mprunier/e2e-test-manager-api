package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.TestResultVariableView;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TestResultVariableResponse(@NotBlank String name, @NotBlank String value) {

  public static TestResultVariableResponse fromTestResultVariableView(
      TestResultVariableView testResultVariableView) {
    return builder()
        .name(testResultVariableView.name())
        .value(testResultVariableView.value())
        .build();
  }
}
