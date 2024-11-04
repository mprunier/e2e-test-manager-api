package fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo;

public record MaxParallelWorkers(int value) {
  public MaxParallelWorkers {
    if (value < 1) {
      throw new IllegalArgumentException("Maximum parallel workers must be at least 1");
    }
  }

  public static MaxParallelWorkers defaultValue() {
    return new MaxParallelWorkers(1);
  }
}
