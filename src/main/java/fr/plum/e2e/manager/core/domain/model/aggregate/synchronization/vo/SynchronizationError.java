package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.time.ZonedDateTime;
import lombok.Builder;

@Builder
public record SynchronizationError(
    SynchronizationFileName file, SynchronizationErrorValue error, ZonedDateTime at) {
  public SynchronizationError {
    Assert.notNull("SynchronizationError SynchronizationFileName", file);
    Assert.notNull("SynchronizationError SynchronizationErrorValue", error);
    Assert.notNull("SynchronizationError ZonedDateTime", at);
  }
}
