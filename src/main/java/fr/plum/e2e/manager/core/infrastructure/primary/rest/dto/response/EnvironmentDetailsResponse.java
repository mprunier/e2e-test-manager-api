package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record EnvironmentDetailsResponse(
    UUID id,
    String description,
    String projectId,
    String branch,
    String token,
    Boolean isEnabled,
    int maxParallelTests,
    List<EnvironmentVariableResponse> variables,
    String createdBy,
    String updatedBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt) {

  public static EnvironmentDetailsResponse from(Environment environment) {
    return new EnvironmentDetailsResponse(
        environment.getId().value(),
        environment.getEnvironmentDescription().value(),
        environment.getSourceCodeInformation().sourceCodeProjectId().value(),
        environment.getSourceCodeInformation().sourceCodeBranch().value(),
        environment.getSourceCodeInformation().sourceCodeToken().getMaskedValue(),
        environment.getIsEnabled().value(),
        environment.getMaxParallelWorkers().value(),
        environment.getVariables().stream().map(EnvironmentVariableResponse::from).toList(),
        environment.getAuditInfo().getCreatedBy().value(),
        environment.getAuditInfo().getUpdatedBy().value(),
        environment.getAuditInfo().getCreatedAt(),
        environment.getAuditInfo().getUpdatedAt());
  }
}
