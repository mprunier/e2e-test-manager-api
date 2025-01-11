package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.command.EnvironmentVariableCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

public record CreateUpdateEnvironmentVariableRequest(
    @NotBlank String name, @NotBlank String value, String description, @NotNull Boolean isHidden) {
  public EnvironmentVariableCommand toCommand() {
    return new EnvironmentVariableCommand(
        new EnvironmentVariableId(name),
        new VariableValue(value),
        StringUtils.isNotBlank(description) ? new VariableDescription(description) : null,
        new VariableIsHidden(isHidden));
  }
}
