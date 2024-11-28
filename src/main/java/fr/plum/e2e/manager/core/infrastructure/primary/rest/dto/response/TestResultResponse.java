package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
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
    List<TestResultVariableResponse> variables) {}
