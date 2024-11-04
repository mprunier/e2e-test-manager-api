package fr.plum.e2e.OLD.events;

import fr.plum.e2e.OLD.domain.enumeration.PipelineType;
import fr.plum.e2e.OLD.domain.event.internal.PipelineCompletedEvent;
import fr.plum.e2e.OLD.domain.event.send.AllTestsPipelinesUpdatedEvent;
import fr.plum.e2e.OLD.domain.event.send.RunCompletedEvent;
import fr.plum.e2e.OLD.domain.event.send.UpdateFinalMetricsEvent;
import fr.plum.e2e.OLD.domain.response.ConfigurationSuiteResponse;
import fr.plum.e2e.OLD.events.queue.PipelineCompletedEventQueueManager;
import fr.plum.e2e.OLD.model.entity.PipelineGroupEntity;
import fr.plum.e2e.OLD.service.CleanPipelineService;
import fr.plum.e2e.OLD.service.TestService;
import fr.plum.e2e.OLD.service.retrieval.ConfigurationSuiteRetrievalService;
import fr.plum.e2e.OLD.service.retrieval.PipelineRetrievalService;
import fr.plum.e2e.OLD.usecases.pipeline.RetrieveAllTestsPipelinesUseCase;
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
  private final TestService testService;
  private final CleanPipelineService cleanPipelineService;

  private final PipelineCompletedEventQueueManager queueManager;

  private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;

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
          "Processing worker consumer for environment: {}, worker ID: {}",
          event.getEnvironmentId(),
          event.getPipelineId());
      var pipelineGroup = pipelineRetrievalService.getGroup(event.getPipelineId());

      if (PipelineType.ALL.equals(event.getType())
          || PipelineType.ALL_IN_PARALLEL.equals(event.getType())) {
        buildAndSendUpdateAllTestsPipelinesEvent(event);
      }

      if (pipelineGroup == null || pipelineGroup.isAllCompleted()) {
        var isAllTests = isAllTests(event);

        validTest(event, pipelineGroup);

        buildAndSendRunCompletedEvent(event, isAllTests);
        updateFinalMetricsEvent.fire(
            UpdateFinalMetricsEvent.builder()
                .environmentId(event.getEnvironmentId())
                .isAllTestsRun(isAllTests)
                .build());

        cleanPipelineService.getLastPipelineGroup(event.getPipelineId());
      }

    } catch (Exception e) {
      log.error(
          "Error processing worker consumer for environment: {}, worker ID: {}",
          event.getEnvironmentId(),
          event.getPipelineId(),
          e);
      throw e;
    }
  }

  private void validTest(PipelineCompletedEvent event, PipelineGroupEntity pipelineGroup) {
    if (pipelineGroup != null) {
      pipelineGroup.getPipelines().forEach(p -> testService.setNotWaiting(p.getId()));
    } else {
      testService.setNotWaiting(event.getPipelineId());
    }
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

    //    sendEventToEnvironmentSessions(runCompletedEvent);
  }

  private void buildAndSendUpdateAllTestsPipelinesEvent(PipelineCompletedEvent event) {
    var pipelines = retrieveAllTestsPipelinesUseCase.execute(event.getEnvironmentId());
    var updateAllTestsPipelinesEvent =
        AllTestsPipelinesUpdatedEvent.builder()
            .environmentId(event.getEnvironmentId())
            .pipelines(pipelines)
            .build();
    //    sendEventToEnvironmentSessions(updateAllTestsPipelinesEvent);
  }
}
