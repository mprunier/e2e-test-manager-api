package fr.plum.e2e.OLD.events;


import fr.plum.e2e.OLD.domain.event.send.AllTestsPipelinesUpdatedEvent;
import fr.plum.e2e.OLD.domain.event.send.RunInProgressEvent;
import fr.plum.e2e.OLD.usecases.pipeline.RetrieveAllTestsPipelinesUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunInProgressEventHandler {

  private final RetrieveAllTestsPipelinesUseCase retrieveAllTestsPipelinesUseCase;

  public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) RunInProgressEvent event) {
    try {
      //      sendEventToEnvironmentSessions(event);
      if (event.getIsAllTests() != null && event.getIsAllTests()) {
        buildAndSendUpdateAllTestsPipelinesEvent(event.getEnvironmentId());
      }
    } catch (Exception e) {
      log.error("Error while sending consumer", e);
    }
  }

  private void buildAndSendUpdateAllTestsPipelinesEvent(Long environmentId) {
    var pipelines = retrieveAllTestsPipelinesUseCase.execute(environmentId);
    var updateAllTestsPipelinesEvent =
        AllTestsPipelinesUpdatedEvent.builder()
            .environmentId(environmentId)
            .pipelines(pipelines)
            .build();
    //    sendEventToEnvironmentSessions(updateAllTestsPipelinesEvent);
  }
}
