package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateFinalMetricsUseCaseTest {

    @Mock
    private EnvironmentRetrievalService environmentRetrievalService;

    @InjectMocks
    private CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;

    @Test
    void shouldCalculateMetricsWhenAllTestsPass() {
        // Given
        var environmentId = 1L;
        var environment = createEnvironmentWithAllPassingTests();
        when(environmentRetrievalService.getEnvironment(environmentId)).thenReturn(environment);

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
        when(environmentRetrievalService.getEnvironment(environmentId)).thenReturn(environment);

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
        when(environmentRetrievalService.getEnvironment(environmentId)).thenReturn(environment);

        // When
        var metrics = calculateFinalMetricsUseCase.execute(environmentId);

        // Then
        assertEquals(0, metrics.passPercent());
    }

    private EnvironmentEntity createEnvironmentWithAllPassingTests() {
        var test = ConfigurationTestEntity.builder().status(ConfigurationStatus.SUCCESS).build();
        var suite = ConfigurationSuiteEntity.builder().configurationTests(Collections.singletonList(test)).build();
        return EnvironmentEntity.builder().configurationSuites(Collections.singletonList(suite)).build();
    }

    private EnvironmentEntity createEnvironmentWithSomeFailingTests() {
        var passingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.SUCCESS).build();
        var failingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.FAILED).build();
        var failingTest2 = ConfigurationTestEntity.builder().status(ConfigurationStatus.FAILED).build();
        var suite = ConfigurationSuiteEntity.builder().configurationTests(Arrays.asList(passingTest, failingTest, failingTest)).build();
        return EnvironmentEntity.builder().configurationSuites(Collections.singletonList(suite)).build();
    }

    private EnvironmentEntity createEnvironmentWithAllFailingTests() {
        var failingTest = ConfigurationTestEntity.builder().status(ConfigurationStatus.FAILED).build();
        var suite = ConfigurationSuiteEntity.builder().configurationTests(Collections.singletonList(failingTest)).build();
        return EnvironmentEntity.builder().configurationSuites(Collections.singletonList(suite)).build();
    }
}