package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.command.CreateEnvironmentCommand;
import fr.plum.e2e.manager.core.domain.model.command.UpdateEnvironmentCommand;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
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

  public UpdateEnvironmentCommand toCommand(UUID environmentId, String username) {
    var sourceCodeInformation =
        SourceCodeInformation.builder().projectId(projectId).token(token).branch(branch).build();
    return new UpdateEnvironmentCommand(
        environmentId != null ? new EnvironmentId(environmentId) : null,
        new EnvironmentDescription(description),
        sourceCodeInformation,
        new MaxParallelWorkers(maxParallelWorkers),
        variables != null
            ? variables.stream().map(CreateUpdateEnvironmentVariableRequest::toCommand).toList()
            : new ArrayList<>(),
        new ActionUsername(username));
  }

  public CreateEnvironmentCommand toCommand(String username) {
    var sourceCodeInformation =
        SourceCodeInformation.builder().projectId(projectId).token(token).branch(branch).build();
    return new CreateEnvironmentCommand(
        new EnvironmentDescription(description),
        sourceCodeInformation,
        new MaxParallelWorkers(maxParallelWorkers),
        variables != null
            ? variables.stream().map(CreateUpdateEnvironmentVariableRequest::toCommand).toList()
            : new ArrayList<>(),
        new ActionUsername(username));
  }
}
