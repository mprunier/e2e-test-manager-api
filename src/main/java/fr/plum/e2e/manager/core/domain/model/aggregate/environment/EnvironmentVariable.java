package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.exception.HiddenVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class EnvironmentVariable extends Entity<EnvironmentVariableId> {

  private VariableValue value;
  private VariableDescription description; // Not the name which is the id
  private VariableIsHidden isHidden;

  EnvironmentVariable(
      EnvironmentVariableId environmentVariableId,
      VariableValue value,
      VariableDescription description,
      VariableIsHidden isHidden) {
    super(environmentVariableId);
    Assert.notNull("VariableValue", value);
    Assert.notNull("VariableDescription", description);
    Assert.notNull("VariableIsHidden", isHidden);
    this.value = value;
    this.description = description;
    this.isHidden = isHidden;
  }

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
