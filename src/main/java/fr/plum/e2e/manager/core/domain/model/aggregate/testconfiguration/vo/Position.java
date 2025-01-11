package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record Position(Integer value) {
  public Position {
    Assert.notNull("Position value", value);
  }
}
