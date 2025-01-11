package fr.plum.e2e.manager.core.application.command.metrics;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryMetricsRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddMetricsCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final MetricsType METRICS_TYPE = MetricsType.ALL;

  private AddMetricsCommandHandler handler;

  private InMemoryMetricsRepositoryAdapter metricsRepository;

  @BeforeEach
  void setUp() {
    metricsRepository = new InMemoryMetricsRepositoryAdapter();
    InMemoryClockAdapter clock = new InMemoryClockAdapter();
    handler = new AddMetricsCommandHandler(metricsRepository, clock);
  }

  @Test
  void should_calculate_metrics_correctly() {
    // Given
    var command = new AddMetricsCommand(ENVIRONMENT_ID, METRICS_TYPE);

    // When
    handler.execute(command);

    // Then
    var optionalMetrics = metricsRepository.findLastMetrics(ENVIRONMENT_ID, METRICS_TYPE);
    assertTrue(optionalMetrics.isPresent());

    var metrics = optionalMetrics.get();
    assertEquals(command.environmentId(), metrics.getEnvironmentId());
    assertEquals(command.metricsType(), metrics.getType());
    assertEquals(10, metrics.getTestCount().value());
    assertEquals(2, metrics.getSuiteCount().value());
    assertEquals(7, metrics.getPassCount().value());
    assertEquals(2, metrics.getFailureCount().value());
    assertEquals(1, metrics.getSkippedCount().value());
    assertEquals(70, metrics.getPassPercentage().value());
  }
}
