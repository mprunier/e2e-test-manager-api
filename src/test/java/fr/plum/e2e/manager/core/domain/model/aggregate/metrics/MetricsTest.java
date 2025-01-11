package fr.plum.e2e.manager.core.domain.model.aggregate.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.FailureCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.MetricsId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.PassCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SkippedCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.SuiteCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.vo.TestCount;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MetricsTest {

  private EnvironmentId environmentId;
  private MetricsType type;
  private TestCount testCount;
  private SuiteCount suiteCount;
  private PassCount passCount;
  private FailureCount failureCount;
  private SkippedCount skippedCount;
  private AuditInfo auditInfo;

  @BeforeEach
  void setUp() {
    // GIVEN
    environmentId = new EnvironmentId(UUID.randomUUID());
    type = MetricsType.SUITE;
    testCount = new TestCount(10);
    suiteCount = new SuiteCount(2);
    passCount = new PassCount(8);
    failureCount = new FailureCount(1);
    skippedCount = new SkippedCount(1);
    auditInfo = AuditInfo.create(new ActionUsername("testUser"), ZonedDateTime.now());
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_metrics_with_valid_data() {
      // WHEN
      Metrics metrics =
          Metrics.create(
              environmentId,
              auditInfo,
              type,
              testCount,
              suiteCount,
              passCount,
              failureCount,
              skippedCount);

      // THEN
      assertThat(metrics).isNotNull();
      assertThat(metrics.getId()).isNotNull();
      assertThat(metrics.getEnvironmentId()).isEqualTo(environmentId);
      assertThat(metrics.getType()).isEqualTo(type);
      assertThat(metrics.getTestCount()).isEqualTo(testCount);
      assertThat(metrics.getSuiteCount()).isEqualTo(suiteCount);
      assertThat(metrics.getPassCount()).isEqualTo(passCount);
      assertThat(metrics.getFailureCount()).isEqualTo(failureCount);
      assertThat(metrics.getSkippedCount()).isEqualTo(skippedCount);
    }

    @Test
    void should_throw_exception_when_environmentId_is_null() {
      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  Metrics.builder()
                      .metricsId(MetricsId.generate())
                      .environmentId(null)
                      .type(type)
                      .testCount(testCount)
                      .suiteCount(suiteCount)
                      .passCount(passCount)
                      .failureCount(failureCount)
                      .skippedCount(skippedCount)
                      .auditInfo(auditInfo)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field EnvironmentId is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_type_is_null() {
      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  Metrics.builder()
                      .metricsId(MetricsId.generate())
                      .environmentId(environmentId)
                      .type(null)
                      .testCount(testCount)
                      .suiteCount(suiteCount)
                      .passCount(passCount)
                      .failureCount(failureCount)
                      .skippedCount(skippedCount)
                      .auditInfo(auditInfo)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field MetricsType is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_testCount_is_null() {
      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  Metrics.builder()
                      .metricsId(MetricsId.generate())
                      .environmentId(environmentId)
                      .type(type)
                      .testCount(null)
                      .suiteCount(suiteCount)
                      .passCount(passCount)
                      .failureCount(failureCount)
                      .skippedCount(skippedCount)
                      .auditInfo(auditInfo)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field TestCount is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class CalculationTests {

    @Test
    void should_calculate_pass_percentage_when_test_count_greater_than_zero() {
      // GIVEN
      Metrics metrics =
          Metrics.create(
              environmentId,
              auditInfo,
              type,
              testCount,
              suiteCount,
              passCount,
              failureCount,
              skippedCount);

      // WHEN
      metrics.calculatePassPercentage();

      // THEN
      assertThat(metrics.getPassPercentage().value()).isEqualTo(80); // (8/10) * 100
    }

    @Test
    void should_not_update_pass_percentage_when_test_count_is_zero() {
      // GIVEN
      Metrics metrics =
          Metrics.create(
              environmentId,
              auditInfo,
              type,
              new TestCount(0),
              suiteCount,
              passCount,
              failureCount,
              skippedCount);

      // WHEN
      metrics.calculatePassPercentage();

      // THEN
      assertThat(metrics.getPassPercentage()).isNull();
    }
  }

  @Nested
  class WorkerTypeConversionTests {

    @Test
    void should_convert_all_worker_types_to_metrics_types() {
      // GIVEN/WHEN/THEN
      assertThat(MetricsType.fromWorkerType(WorkerType.GROUP)).isEqualTo(MetricsType.GROUP);
      assertThat(MetricsType.fromWorkerType(WorkerType.FILE)).isEqualTo(MetricsType.FILE);
      assertThat(MetricsType.fromWorkerType(WorkerType.SUITE)).isEqualTo(MetricsType.SUITE);
      assertThat(MetricsType.fromWorkerType(WorkerType.TEST)).isEqualTo(MetricsType.TEST);
      assertThat(MetricsType.fromWorkerType(WorkerType.ALL)).isEqualTo(MetricsType.ALL);
    }

    @Test
    void should_throw_exception_when_worker_type_is_null() {
      // GIVEN/WHEN/THEN
      assertThatThrownBy(() -> MetricsType.fromWorkerType(null))
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field workerType is mandatory and cannot be empty or null.");
    }
  }
}
