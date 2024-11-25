package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.aggregate.metric.Metrics;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.domain.model.query.GetAllMetricsQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetMetricsQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.MetricsRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.metrics.AddMetricsUseCase;
import fr.plum.e2e.manager.core.domain.usecase.metrics.GetLastMetricsUseCase;
import fr.plum.e2e.manager.core.domain.usecase.metrics.ListAllMetricsUseCase;
import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class MetricsFacade {

  private final AddMetricsUseCase addMetricsUseCase;
  private final GetLastMetricsUseCase getLastMetricsUseCase;
  private final ListAllMetricsUseCase listAllMetricsUseCase;

  public MetricsFacade(ClockPort clockPort, MetricsRepositoryPort metricsRepositoryPort) {
    this.listAllMetricsUseCase = new ListAllMetricsUseCase(metricsRepositoryPort);
    this.addMetricsUseCase = new AddMetricsUseCase(metricsRepositoryPort, clockPort);
    getLastMetricsUseCase = new GetLastMetricsUseCase(metricsRepositoryPort);
  }

  @Transactional
  public void addMetrics(AddMetricsCommand command) {
    addMetricsUseCase.execute(command);
  }

  public List<Metrics> getAllMetrics(GetAllMetricsQuery query) {
    return listAllMetricsUseCase.execute(query);
  }

  public Metrics getLastMetrics(GetMetricsQuery query) {
    return getLastMetricsUseCase.execute(query);
  }
}
