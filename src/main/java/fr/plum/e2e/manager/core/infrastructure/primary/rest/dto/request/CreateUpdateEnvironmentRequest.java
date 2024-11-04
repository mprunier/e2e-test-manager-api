package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeBranch;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeProjectId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeToken;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.command.CreateUpdateEnvironmentCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record CreateUpdateEnvironmentRequest(
    @NotBlank String description,
    @NotBlank String projectId,
    @NotBlank String token,
    @NotBlank String branch,
    @Max(value = 8, message = "maxParallelWorkers must not exceed 8") @NotNull
        Integer maxParallelWorkers,
    List<CreateUpdateEnvironmentVariableRequest> variables) {

  public CreateUpdateEnvironmentRequest {
    if (variables == null) {
      variables = new ArrayList<>();
    }
  }

  public CreateUpdateEnvironmentCommand toCommand(UUID environmentId, String username) {
    var sourceCodeInformation =
        new SourceCodeInformation(
            new SourceCodeProjectId(projectId),
            new SourceCodeToken(token),
            new SourceCodeBranch(branch));
    return new CreateUpdateEnvironmentCommand(
        environmentId != null ? new EnvironmentId(environmentId) : null,
        new EnvironmentDescription(description),
        sourceCodeInformation,
        new MaxParallelWorkers(maxParallelWorkers),
        variables.stream().map(CreateUpdateEnvironmentVariableRequest::toCommand).toList(),
        new ActionUsername(username));
  }
}
