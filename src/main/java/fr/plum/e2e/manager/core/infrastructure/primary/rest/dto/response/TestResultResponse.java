package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TestResultResponse(
    UUID id,
    TestResultStatus status,
    String reference,
    ZonedDateTime createdAt,
    String errorUrl,
    Integer duration,
    String createdBy,
    List<TestResultScreenshotResponse> screenshots,
    Boolean hasVideo,
    List<TestResultVariableResponse> variables) {

  public static TestResultResponse fromTestResultView(TestResultView testResultView) {
    return builder()
        .id(testResultView.id())
        .status(testResultView.status())
        .reference(testResultView.reference())
        .createdAt(testResultView.createdAt())
        .errorUrl(testResultView.errorUrl())
        .duration(testResultView.duration())
        .createdBy(testResultView.createdBy())
        .screenshots(
            testResultView.screenshots().stream()
                .map(TestResultScreenshotResponse::fromTestResultScreenshotView)
                .toList())
        .hasVideo(testResultView.hasVideo())
        .variables(
            testResultView.variables().stream()
                .map(TestResultVariableResponse::fromTestResultVariableView)
                .toList())
        .build();
  }

  public static List<TestResultResponse> fromTestResultViews(List<TestResultView> testResultViews) {
    return testResultViews.stream().map(TestResultResponse::fromTestResultView).toList();
  }
}
