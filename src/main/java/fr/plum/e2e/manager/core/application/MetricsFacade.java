package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.metrics.AddMetricsUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MetricsFacade {

  private final AddMetricsUseCase addMetricsUseCase;

  public MetricsFacade(ClockPort clockPort, MetricsRepositoryPort metricsRepositoryPort) {
    this.addMetricsUseCase = new AddMetricsUseCase(metricsRepositoryPort, clockPort);
  }

  @Transactional
  public void addMetrics(AddMetricsCommand command) {
    addMetricsUseCase.execute(command);
  }
}
