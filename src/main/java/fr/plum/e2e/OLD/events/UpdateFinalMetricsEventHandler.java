package fr.plum.e2e.OLD.events;

import fr.plum.e2e.OLD.domain.event.send.UpdateFinalMetricsEvent;
import fr.plum.e2e.OLD.service.retrieval.MetricRetrievalService;
import fr.plum.e2e.OLD.usecases.metrics.AddMetricsUseCase;
import fr.plum.e2e.OLD.usecases.metrics.CalculateFinalMetricsUseCase;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
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
      addMetricsUseCase.execute(event.getEnvironmentId(), finalMetrics, event.getIsAllTestsRun());
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
              .isAllTestsRun(event.getIsAllTestsRun())
              //              .lastAllTestsRunAt(
              //                  event.getIsAllTestsRun()
              //                      ? finalMetrics.at()
              //                      : optionalLastMetricsWithAllTestsRun
              //                          .map(MetricsEntity::getCreatedAt)
              //                          .orElse(null))
              .build();
      event.setMetrics(metricsResponse);
      //      sendEventToEnvironmentSessions(event);
    } catch (Exception e) {
      log.error("Error while sending consumer", e);
    }
  }
}
