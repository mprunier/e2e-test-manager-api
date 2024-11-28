package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.TestResultVariableView;
import lombok.Builder;

@Builder
public record TestResultVariableResponse(String name, String value) {

  public static TestResultVariableResponse fromTestResultVariableView(
      TestResultVariableView testResultVariableView) {
    return builder()
        .name(testResultVariableView.name())
        .value(testResultVariableView.value())
        .build();
  }
}
