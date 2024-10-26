package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.event.internal.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.send.AllTestsPipelinesUpdatedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.send.RunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.send.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.events.queue.PipelineCompletedEventQueueManager;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RetrieveAllTestsPipelinesUseCase;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineCompletedEventHandler {

  private final RetrieveAllTestsPipelinesUseCase retrieveAllTestsPipelinesUseCase;
  private final PipelineRetrievalService pipelineRetrievalService;
  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;
  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;
  private final PipelineCompletedEventQueueManager queueManager;

  @PostConstruct
  public void init() {
    queueManager.setEventProcessor(this::processEvent);
  }

  public void onPipelineCompleted(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) PipelineCompletedEvent event) {
    queueManager.submitEvent(event);
  }

  private void processEvent(PipelineCompletedEvent event) {
    try {
      log.info(
          "Processing pipeline event for environment: {}, pipeline ID: {}",
          event.getEnvironmentId(),
          event.getPipelineId());
      var isPipelinesGroupFinish =
          pipelineRetrievalService.isPipelinesGroupFinish(event.getPipelineId());

      buildAndSendUpdateAllTestsPipelinesEvent(event);

      if (isPipelinesGroupFinish) {
        var isAllTests = isAllTests(event);
        buildAndSendRunCompletedEvent(event, isAllTests);
        fireUpdateFinalMetricsEvent(event, isAllTests);
      }

    } catch (Exception e) {
      log.error(
          "Error processing pipeline event for environment: {}, pipeline ID: {}",
          event.getEnvironmentId(),
          event.getPipelineId(),
          e);
      throw e;
    }
  }

  private void fireUpdateFinalMetricsEvent(PipelineCompletedEvent event, boolean isAllTests) {
    updateFinalMetricsEvent.fire(
        UpdateFinalMetricsEvent.builder()
            .environmentId(event.getEnvironmentId())
            .isAllTestsRun(isAllTests)
            .build());
  }

  private static boolean isAllTests(PipelineCompletedEvent event) {
    return PipelineType.ALL_IN_PARALLEL.equals(event.getType())
        || PipelineType.ALL.equals(event.getType());
  }

  private void buildAndSendRunCompletedEvent(PipelineCompletedEvent event, boolean isAllTests) {
    ConfigurationSuiteResponse configurationSuiteResponse = null;
    if (!isAllTests) {
      configurationSuiteResponse =
          configurationSuiteRetrievalService.getConfigurationSuiteResponse(
              event.getEnvironmentId(),
              Long.valueOf(event.getConfigurationTestIdsFilter().getFirst()));
    }

    var runCompletedEvent =
        RunCompletedEvent.builder()
            .environmentId(event.getEnvironmentId())
            .isAllTests(isAllTests)
            .configurationSuite(configurationSuiteResponse)
            .build();

    sendEventToEnvironmentSessions(runCompletedEvent);
  }

  private void buildAndSendUpdateAllTestsPipelinesEvent(PipelineCompletedEvent event) {
    var pipelines = retrieveAllTestsPipelinesUseCase.execute(event.getEnvironmentId());
    var updateAllTestsPipelinesEvent =
        AllTestsPipelinesUpdatedEvent.builder()
            .environmentId(event.getEnvironmentId())
            .pipelines(pipelines)
            .build();
    sendEventToEnvironmentSessions(updateAllTestsPipelinesEvent);
  }
}
