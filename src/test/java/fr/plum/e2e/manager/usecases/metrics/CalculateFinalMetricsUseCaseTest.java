package fr.plum.e2e.manager.usecases.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import fr.plum.e2e.OLD.model.entity.ConfigurationSuiteEntity;
import fr.plum.e2e.OLD.model.entity.ConfigurationTestEntity;
import fr.plum.e2e.OLD.model.entity.EnvironmentEntity;
import fr.plum.e2e.OLD.service.retrieval.EnvironmentRetrievalService;
import fr.plum.e2e.OLD.usecases.metrics.CalculateFinalMetricsUseCase;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateFinalMetricsUseCaseTest {

  @Mock private EnvironmentRetrievalService environmentRetrievalService;

  @InjectMocks private CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;

  @Test
  void shouldCalculateMetricsWhenAllTestsPass() {
    // Given
    var environmentId = 1L;
    var environment = createEnvironmentWithAllPassingTests();
    when(environmentRetrievalService.get(environmentId)).thenReturn(environment);

    // When
    var metrics = calculateFinalMetricsUseCase.execute(environmentId);

    // Then
    assertEquals(100, metrics.passPercent());
  }

  @Test
  void shouldCalculateMetricsWhenSomeTestsFail() {
    // Given
    var environmentId = 1L;
    var environment = createEnvironmentWithSomeFailingTests();
    when(environmentRetrievalService.get(environmentId)).thenReturn(environment);

    // When
    var metrics = calculateFinalMetricsUseCase.execute(environmentId);

    // Then
    assertEquals(33, metrics.passPercent());
  }

  @Test
  void shouldCalculateMetricsWhenAllTestsFail() {
    // Given
    var environmentId = 1L;
    var environment = createEnvironmentWithAllFailingTests();
    when(environmentRetrievalService.get(environmentId)).thenReturn(environment);

    // When
    var metrics = calculateFinalMetricsUseCase.execute(environmentId);

    // Then
    assertEquals(0, metrics.passPercent());
  }

  private EnvironmentEntity createEnvironmentWithAllPassingTests() {
    var test = ConfigurationTestEntity.builder().status(ConfigurationStatus.SUCCESS).build();
    var suite =
        ConfigurationSuiteEntity.builder()
            .configurationTests(Collections.singletonList(test))
            .build();
    return EnvironmentEntity.builder()
        .configurationSuites(Collections.singletonList(suite))
        .build();
  }

  private EnvironmentEntity createEnvironmentWithSomeFailingTests() {
    var passingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.SUCCESS).build();
    var failingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.FAILED).build();
    var suite =
        ConfigurationSuiteEntity.builder()
            .configurationTests(Arrays.asList(passingTest, failingTest, failingTest))
            .build();
    return EnvironmentEntity.builder()
        .configurationSuites(Collections.singletonList(suite))
        .build();
  }

  private EnvironmentEntity createEnvironmentWithAllFailingTests() {
    var failingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.FAILED).build();
    var suite =
        ConfigurationSuiteEntity.builder()
            .configurationTests(Collections.singletonList(failingTest))
            .build();
    return EnvironmentEntity.builder()
        .configurationSuites(Collections.singletonList(suite))
        .build();
  }
}
