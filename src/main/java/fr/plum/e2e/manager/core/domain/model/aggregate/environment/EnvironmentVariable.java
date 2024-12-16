package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.exception.HiddenVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class EnvironmentVariable extends Entity<EnvironmentVariableId> {

  private VariableValue value;
  private VariableDescription description;
  private VariableIsHidden isHidden;

  public boolean isValueMasked() {
    return value.value().contains("**********");
  }

  public EnvironmentVariable updateFrom(EnvironmentVariable other) {
    if (isHidden.value() && other.isValueMasked()) {
      if (!other.isHidden.value()) {
        throw new HiddenVariableException();
      }
      return builder()
          .id(other.getId())
          .value(value)
          .description(other.description)
          .isHidden(other.isHidden)
          .build();
    }
    return other;
  }
}
