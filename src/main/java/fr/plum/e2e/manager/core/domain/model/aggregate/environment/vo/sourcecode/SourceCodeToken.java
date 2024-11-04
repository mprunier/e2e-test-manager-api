package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode;

public record SourceCodeToken(String value) {

  public String getMaskedValue() {
    if (value.length() <= 6) return "**********";

    var masked = new StringBuilder(value);
    for (int i = 3; i < value.length() - 3; i++) {
      masked.setCharAt(i, '*');
    }
    return masked.toString();
  }
}
