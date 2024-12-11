package fr.plum.e2e.manager.core.domain.model.view;


public record EnvironmentDetailsVariableView(
    String name, String value, String description, Boolean isHidden) {

  public String getDisplayValue() {
    return isHidden ? "**********" : value;
  }
}
 