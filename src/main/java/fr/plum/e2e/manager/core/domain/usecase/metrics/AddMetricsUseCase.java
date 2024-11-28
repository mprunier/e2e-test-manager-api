package fr.plum.e2e.manager.core.domain.usecase.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;

public class AddMetricsUseCase implements CommandUseCase<AddMetricsCommand> {

  private final ClockPort clockPort;
  private final MetricsRepositoryPort metricsRepositoryPort;

  public AddMetricsUseCase(MetricsRepositoryPort metricsRepositoryPort, ClockPort clockPort) {
    this.metricsRepositoryPort = metricsRepositoryPort;
    this.clockPort = clockPort;
  }

  @Override
  public void execute(AddMetricsCommand command) {

    var testCount = metricsRepositoryPort.testCount(command.environmentId());
    var suiteCount = metricsRepositoryPort.suiteCount(command.environmentId());
    var passCount = metricsRepositoryPort.passCount(command.environmentId());
    var failureCount = metricsRepositoryPort.failureCount(command.environmentId());
    var skippedCount = metricsRepositoryPort.skippedCount(command.environmentId());

    var metrics =
        Metrics.initialize(command.environmentId(), clockPort.now(), command.metricsType());
    metrics.addCounts(testCount, suiteCount, passCount, failureCount, skippedCount);
    metrics.createAuditInfo(clockPort.now());
    metrics.calculatePassPercentage();

    metricsRepositoryPort.save(metrics);
  }
}
