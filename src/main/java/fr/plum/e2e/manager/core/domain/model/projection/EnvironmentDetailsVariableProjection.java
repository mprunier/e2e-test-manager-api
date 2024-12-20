package fr.plum.e2e.manager.core.domain.model.projection;

public record EnvironmentDetailsVariableProjection(
    String name, String value, String description, Boolean isHidden) {

  public String getDisplayValue() {
    return isHidden ? "**********" : value;
  }
}
