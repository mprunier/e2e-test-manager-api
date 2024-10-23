package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.MetricsEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.MetricRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.AddMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateFinalMetricsEventHandler {

  private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;
  private final AddMetricsUseCase addMetricsUseCase;
  private final MetricRetrievalService metricRetrievalService;

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) UpdateFinalMetricsEvent event) {
    try {
      var finalMetrics = calculateFinalMetricsUseCase.execute(event.getEnvironmentId());
      addMetricsUseCase.execute(event.getEnvironmentId(), finalMetrics, event.isAllTestsRun());
      var optionalLastMetricsWithAllTestsRun =
          metricRetrievalService.getOptionalLastMetricsWithAllTestsRun(event.getEnvironmentId());
      var metricsResponse =
          MetricsResponse.builder()
              .at(finalMetrics.at())
              .suites(finalMetrics.suites())
              .tests(finalMetrics.tests())
              .passPercent(finalMetrics.passPercent())
              .passes(finalMetrics.passes())
              .failures(finalMetrics.failures())
              .skipped(finalMetrics.skipped())
              .isAllTestsRun(event.isAllTestsRun())
              .lastAllTestsRunAt(
                  optionalLastMetricsWithAllTestsRun.map(MetricsEntity::getCreatedAt).orElse(null))
              .build();
      event.setMetrics(metricsResponse);
      sendEventToEnvironmentSessions(event);
    } catch (Exception e) {
      log.error("Error while sending event", e);
    }
  }
}
