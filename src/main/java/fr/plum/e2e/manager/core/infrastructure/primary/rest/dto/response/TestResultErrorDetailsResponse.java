package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.TestResultErrorDetailsProjection;
import lombok.Builder;

@Builder
public record TestResultErrorDetailsResponse(
    String errorMessage, String errorStacktrace, String code) {

  public static TestResultErrorDetailsResponse fromDomain(
      TestResultErrorDetailsProjection testResultErrorDetails) {
    return builder()
        .errorMessage(testResultErrorDetails.errorMessage())
        .errorStacktrace(testResultErrorDetails.errorStacktrace())
        .code(testResultErrorDetails.code())
        .build();
  }
}
