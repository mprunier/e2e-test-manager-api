package fr.njj.galaxion.endtoendtesting.events;

import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.EnvironmentService;
import fr.njj.galaxion.endtoendtesting.service.ParallelPipelineProgressService;
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
  private final ParallelPipelineProgressService parallelPipelineProgressService;
  private final EnvironmentService environmentService;

  private final Event<AllTestsRunCompletedEvent> allTestsRunCompletedEvent;

  public void send(
      @Observes(during = TransactionPhase.AFTER_SUCCESS) PipelineCompletedEvent event) {

    var pipeline = pipelineRetrievalService.get(event.getPipelineId());
    var environment = pipeline.getEnvironment();
    parallelPipelineProgressService.incrementCompletedPipelines(pipeline.getId());

    if (parallelPipelineProgressService.isAllCompleted(pipeline.getId())) {
      environmentService.endAllTestsRun(environment.getId());
      allTestsRunCompletedEvent.fire(
          AllTestsRunCompletedEvent.builder().environmentId(environment.getId()).build());
    }
  }
}
