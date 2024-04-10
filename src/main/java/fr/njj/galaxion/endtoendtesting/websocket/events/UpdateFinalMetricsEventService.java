package fr.njj.galaxion.endtoendtesting.websocket.events;

import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEvents.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateFinalMetricsEventService {

    @Transactional
    public void send(Long environmentId,
                     Metrics metrics) {
        try {
            var metricsResponse = MetricsResponse
                    .builder()
                    .at(metrics.at())
                    .suites(metrics.suites())
                    .tests(metrics.tests())
                    .passPercent(metrics.passPercent())
                    .passes(metrics.passes())
                    .failures(metrics.failures())
                    .skipped(metrics.skipped())
                    .build();
            var event = UpdateFinalMetricsEvent
                    .builder()
                    .metrics(metricsResponse)
                    .build();
            sendEventToEnvironmentSessions(environmentId.toString(), event);
        } catch (Exception e) {
            log.error("Error while sending event", e);
        }
    }
}

