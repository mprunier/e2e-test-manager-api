package fr.plum.e2e.manager.core.application.command.metrics;

import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.port.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.application.command.CommandHandler;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AddMetricsCommandHandler implements CommandHandler<AddMetricsCommand> {

  private final ClockPort clockPort;
  private final MetricsRepositoryPort metricsRepositoryPort;

  public AddMetricsCommandHandler(
      MetricsRepositoryPort metricsRepositoryPort, ClockPort clockPort) {
    this.metricsRepositoryPort = metricsRepositoryPort;
    this.clockPort = clockPort;
  }

  @Override
  @Transactional
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
