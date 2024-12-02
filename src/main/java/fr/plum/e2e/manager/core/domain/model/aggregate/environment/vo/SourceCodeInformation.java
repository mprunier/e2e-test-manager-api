package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import lombok.Builder;

@Builder
public record SourceCodeInformation(String projectId, String token, String branch) {

  public SourceCodeInformation {
    Assert.notBlank("source code project id", projectId);
    Assert.notBlank("source code token", token);
    Assert.notBlank("source code branch", branch);
  }

  public String getMaskedValue() {
    if (token.length() <= 6) return "**********";

    var masked = new StringBuilder(token);
    for (int i = 3; i < token.length() - 3; i++) {
      masked.setCharAt(i, '*');
    }
    return masked.toString();
  }
}
