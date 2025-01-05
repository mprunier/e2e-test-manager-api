package fr.plum.e2e.manager.core.application.command.metrics;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.Metrics;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.model.command.AddMetricsCommand;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemoryMetricsRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddMetricsCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final MetricsType METRICS_TYPE = MetricsType.ALL;

  private AddMetricsCommandHandler handler;
  private InMemoryMetricsRepositoryAdapter metricsRepository;
  private InMemoryClockAdapter clock;

  @BeforeEach
  void setUp() {
    metricsRepository = new InMemoryMetricsRepositoryAdapter();
    clock = new InMemoryClockAdapter();
    handler = new AddMetricsCommandHandler(metricsRepository, clock);
  }

  @Test
  void should_calculate_metrics_correctly() {
    // Given
    var command = new AddMetricsCommand(ENVIRONMENT_ID, METRICS_TYPE);
    setupExistingMetrics();

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
    assertEquals(8, metrics.getPassCount().value());
    assertEquals(1, metrics.getFailureCount().value());
    assertEquals(1, metrics.getSkippedCount().value());
    assertEquals(80, metrics.getPassPercentage().value());
  }

  private void setupExistingMetrics() {
    var existingMetrics =
        Metrics.create(
            ENVIRONMENT_ID,
            AuditInfo.create(clock.now()),
            METRICS_TYPE,
            new TestCount(10),
            new SuiteCount(2),
            new PassCount(8),
            new FailureCount(1),
            new SkippedCount(1));
    metricsRepository.save(existingMetrics);
  }
}
