package fr.njj.galaxion.endtoendtesting.events;

import static fr.njj.galaxion.endtoendtesting.websocket.WebSocketEventHandler.sendEventToEnvironmentSessions;

import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.RunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.PipelineGroupService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
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

  private final PipelineRetrievalService pipelineRetrievalService;
  private final PipelineGroupService pipelineGroupService;

  private final Event<RunCompletedEvent> allTestsRunCompletedEvent;

  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) PipelineCompletedEvent event) {

    var pipelineGroup = pipelineGroupService.get(event.getPipelineId());

    if (pipelineGroup == null || pipelineGroup.isAllCompleted()) {
      var environment = pipelineRetrievalService.getEnvironment(event.getPipelineId());
      allTestsRunCompletedEvent.fire(
          RunCompletedEvent.builder().environmentId(environment.getId()).build());
    } else {
      sendEventToEnvironmentSessions(event);
    }
  }
}
