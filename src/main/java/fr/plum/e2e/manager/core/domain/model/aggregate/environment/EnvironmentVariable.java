package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class EnvironmentVariable extends Entity<EnvironmentVariableId> {

  private VariableValue value;
  private VariableDescription description;
  private VariableIsHidden isHidden;

  public VariableValue getDisplayValue() {
    return isHidden.value() ? new VariableValue("**********") : value;
  }
}
