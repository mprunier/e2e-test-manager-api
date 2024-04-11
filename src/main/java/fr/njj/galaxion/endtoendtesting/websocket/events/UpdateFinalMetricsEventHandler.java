package fr.njj.galaxion.endtoendtesting.websocket.events;

import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateFinalMetricsEventHandler {

    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;

    @Monitored
    public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) UpdateFinalMetricsEvent event) {
        try {
            var finalMetrics = calculateFinalMetricsUseCase.execute(event.getEnvironmentId());
            var metricsResponse = MetricsResponse
                    .builder()
                    .at(finalMetrics.at())
                    .suites(finalMetrics.suites())
                    .tests(finalMetrics.tests())
                    .passPercent(finalMetrics.passPercent())
                    .passes(finalMetrics.passes())
                    .failures(finalMetrics.failures())
                    .skipped(finalMetrics.skipped())
                    .build();
            event.setMetrics(metricsResponse);
            sendEventToEnvironmentSessions(event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}

