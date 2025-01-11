package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.exception.DuplicateEnvironmentVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import java.util.List;

public record UpdateEnvironmentCommand(
    EnvironmentId environmentId,
    EnvironmentDescription description,
    SourceCodeInformation sourceCodeInformation,
    MaxParallelWorkers maxParallelWorkers,
    List<EnvironmentVariableCommand> variables,
    ActionUsername actionUsername) {

  public UpdateEnvironmentCommand {
    if (variables.stream().map(EnvironmentVariableCommand::name).distinct().count()
        != variables.size()) {
      throw new DuplicateEnvironmentVariableException("Duplicate variable id");
    }
  }

  public List<EnvironmentVariable> toDomainVariables() {
    return variables.stream().map(EnvironmentVariableCommand::toDomain).toList();
  }
}
