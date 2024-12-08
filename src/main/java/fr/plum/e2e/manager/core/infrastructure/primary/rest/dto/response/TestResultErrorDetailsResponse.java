package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailsView;
import lombok.Builder;

@Builder
public record TestResultErrorDetailsResponse(
    String errorMessage, String errorStacktrace, String code) {

  public static TestResultErrorDetailsResponse fromDomain(
      TestResultErrorDetailsView testResultErrorDetails) {
    return builder()
        .errorMessage(testResultErrorDetails.errorMessage())
        .errorStacktrace(testResultErrorDetails.errorStacktrace())
        .code(testResultErrorDetails.code())
        .build();
  }
}
