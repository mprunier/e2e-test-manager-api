package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.RunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.service.PipelineGroupService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineCompletedEventHandler {

  private final PipelineGroupService pipelineGroupService;
  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS)
          PipelineCompletedEvent pipelineCompletedEvent) {

    var pipelineGroup = pipelineGroupService.get(pipelineCompletedEvent.getPipelineId());

    if (pipelineGroup == null || pipelineGroup.isAllCompleted()) {
      var isAllTests =
          PipelineType.ALL_IN_PARALLEL.equals(pipelineCompletedEvent.getType())
              || PipelineType.ALL.equals(pipelineCompletedEvent.getType());
      buildAndSendRunCompletedEvent(pipelineCompletedEvent, isAllTests);
    } else {
      sendEventToEnvironmentSessions(pipelineCompletedEvent);
    }
  }

  private void buildAndSendRunCompletedEvent(
      PipelineCompletedEvent pipelineCompletedEvent, boolean isAllTests) {

    ConfigurationSuiteResponse configurationSuiteResponse = null;
    if (!isAllTests) {
      configurationSuiteResponse =
          configurationSuiteRetrievalService.getConfigurationSuiteResponse(
              pipelineCompletedEvent.getEnvironmentId(),
              Long.valueOf(pipelineCompletedEvent.getConfigurationTestIdsFilter().getFirst()));
    }

    var runCompletedEvent =
        RunCompletedEvent.builder()
            .environmentId(pipelineCompletedEvent.getEnvironmentId())
            .isAllTests(isAllTests)
            .configurationSuite(configurationSuiteResponse)
            .build();

    sendEventToEnvironmentSessions(runCompletedEvent);
  }
}
