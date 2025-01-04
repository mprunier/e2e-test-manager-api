package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.exception.HiddenVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EnvironmentVariable extends Entity<EnvironmentVariableId> {

  private VariableValue value;
  private VariableDescription description; // Not the name which is the id
  private VariableIsHidden isHidden;

  @Builder
  public EnvironmentVariable(
      EnvironmentVariableId environmentVariableId,
      VariableValue value,
      VariableDescription description,
      VariableIsHidden isHidden) {
    super(environmentVariableId);
    Assert.notNull("value", value);
    Assert.notNull("description", description);
    Assert.notNull("isHidden", isHidden);
    this.value = value;
    this.description = description;
    this.isHidden = isHidden;
  }

  public static EnvironmentVariable create(
      EnvironmentVariableId name,
      VariableValue value,
      VariableDescription description,
      VariableIsHidden isHidden) {
    return builder()
        .environmentVariableId(name)
        .value(value)
        .description(description)
        .isHidden(isHidden)
        .build();
  }

  public boolean isValueMasked() {
    return value.value().contains("**********");
  }

  public EnvironmentVariable update(EnvironmentVariable newVariable) {
    if (isHidden.value() && newVariable.isValueMasked()) {
      if (!newVariable.isHidden.value()) {
        throw new HiddenVariableException();
      }
      return builder()
          .environmentVariableId(id)
          .value(value)
          .description(newVariable.description)
          .isHidden(newVariable.isHidden)
          .build();
    }
    return newVariable;
  }
}
