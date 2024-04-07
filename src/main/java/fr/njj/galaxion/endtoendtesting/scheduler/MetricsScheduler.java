package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.AddMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class MetricsScheduler {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;
    private final AddMetricsUseCase addMetricsUseCase;

    @Scheduled(every = "1h")
    @ActivateRequestContext
    public void execute() {
        var environments = environmentRetrievalService.getEnvironments();
        environments.forEach(environment -> {
            var finalMetrics = calculateFinalMetricsUseCase.execute(environment.getId());
            addMetricsUseCase.execute(environment.getId(), finalMetrics);
        });
    }
}
