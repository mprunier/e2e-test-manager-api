package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import lombok.Builder;

@Builder
public record SourceCodeInformation(String projectId, String token, String branch) {

  public SourceCodeInformation {
    Assert.notBlank("source code projectId", projectId);
    Assert.notBlank("source code token", token);
    Assert.notBlank("source code branch", branch);
  }
}
