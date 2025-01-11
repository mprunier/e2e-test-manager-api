package fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo;

public record SynchronizationIsInProgress(boolean value) {
  public static SynchronizationIsInProgress defaultStatus() {
    return new SynchronizationIsInProgress(false);
  }

  public SynchronizationIsInProgress start() {
    return new SynchronizationIsInProgress(true);
  }

  public SynchronizationIsInProgress finish() {
    return new SynchronizationIsInProgress(false);
  }
}
