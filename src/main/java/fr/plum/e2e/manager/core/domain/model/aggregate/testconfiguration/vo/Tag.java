package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record Tag(String value) {
  public Tag {
    Assert.notBlank("Tag value", value);
  }
}
