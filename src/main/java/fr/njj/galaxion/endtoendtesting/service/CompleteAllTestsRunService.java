package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CompleteAllTestsRunService {

  private final PipelineRetrievalService pipelineRetrievalService;

  private final Event<PipelineCompletedEvent> allTestsRunCompletedEvent;

  @Transactional
  public void complete(String pipelineId, ReportPipelineStatus reportPipelineStatus) {

    var pipeline = pipelineRetrievalService.get(pipelineId);
    if (reportPipelineStatus != null) {
      pipeline.setReportError(reportPipelineStatus.getErrorMessage());
    } else {
      pipeline.setReportError(null);
    }

    allTestsRunCompletedEvent.fire(PipelineCompletedEvent.builder().pipelineId(pipelineId).build());
  }
}
