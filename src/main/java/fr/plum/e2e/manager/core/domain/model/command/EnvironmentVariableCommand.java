package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;

public record EnvironmentVariableCommand(
    EnvironmentVariableId name,
    VariableValue value,
    VariableDescription description,
    VariableIsHidden isHidden) {

  public static EnvironmentVariable toDomain(EnvironmentVariableCommand command) {
    return EnvironmentVariable.builder()
        .environmentVariableId(command.name())
        .value(command.value())
        .description(command.description())
        .isHidden(command.isHidden())
        .build();
  }
}
