package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SynchronizationErrorValue(String value) {
  public SynchronizationErrorValue {
    Assert.notNull("SynchronizationErrorValue", value);
  }
}
