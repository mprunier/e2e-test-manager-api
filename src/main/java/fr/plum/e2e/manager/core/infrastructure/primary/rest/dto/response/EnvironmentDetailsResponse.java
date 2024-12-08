package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsView;
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
    @NotNull Integer maxParallelWorkers,
    @NotNull Boolean synchronizationInProgress,
    List<EnvironmentVariableResponse> variables,
    String createdBy,
    String updatedBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt) {

  public static EnvironmentDetailsResponse from(EnvironmentDetailsView environment) {
    return new EnvironmentDetailsResponse(
        environment.id(),
        environment.description(),
        environment.projectId(),
        environment.branch(),
        environment.getMaskedValue(),
        environment.isEnabled(),
        environment.maxParallelWorkers(),
        environment.synchronizationInProgress(),
        environment.variables().stream().map(EnvironmentVariableResponse::from).toList(),
        environment.createdBy(),
        environment.updatedBy(),
        environment.createdAt(),
        environment.updatedAt());
  }
}
