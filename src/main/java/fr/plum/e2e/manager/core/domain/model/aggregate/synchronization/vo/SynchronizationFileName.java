package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SynchronizationFileName(String value) {
  public SynchronizationFileName {
    Assert.notNull("SynchronizationFileName", value);
  }
}
