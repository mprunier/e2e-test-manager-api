package fr.plum.e2e.manager.core.domain.model.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TestResultProjection(
    UUID id,
    TestResultStatus status,
    String reference,
    ZonedDateTime createdAt,
    String errorUrl,
    Integer duration,
    String createdBy,
    List<TestResultScreenshotProjection> screenshots,
    UUID videoId,
    List<TestResultVariableProjection> variables) {}
