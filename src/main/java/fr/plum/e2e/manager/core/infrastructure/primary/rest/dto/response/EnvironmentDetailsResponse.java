package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record EnvironmentDetailsResponse(
    @NotNull UUID id,
    String description,
    @NotBlank String projectId,
    @NotBlank String branch,
    @NotBlank String token,
    @NotNull Boolean isEnabled,
    @NotNull int maxParallelWorkers,
    List<EnvironmentVariableResponse> variables,
    String createdBy,
    String updatedBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt) {

  public static EnvironmentDetailsResponse from(Environment environment) {
    return new EnvironmentDetailsResponse(
        environment.getId().value(),
        environment.getEnvironmentDescription().value(),
        environment.getSourceCodeInformation().projectId(),
        environment.getSourceCodeInformation().branch(),
        environment.getSourceCodeInformation().getMaskedValue(),
        environment.getIsEnabled().value(),
        environment.getMaxParallelWorkers().value(),
        environment.getVariables().stream().map(EnvironmentVariableResponse::from).toList(),
        environment.getAuditInfo().getCreatedBy().value(),
        environment.getAuditInfo().getUpdatedBy().value(),
        environment.getAuditInfo().getCreatedAt(),
        environment.getAuditInfo().getUpdatedAt());
  }
}
