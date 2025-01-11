package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

public record EnvironmentIsEnabled(boolean value) {
  public static EnvironmentIsEnabled defaultStatus() {
    return new EnvironmentIsEnabled(true);
  }

  public static EnvironmentIsEnabled enabled() {
    return new EnvironmentIsEnabled(true);
  }

  public static EnvironmentIsEnabled disabled() {
    return new EnvironmentIsEnabled(false);
  }
}
