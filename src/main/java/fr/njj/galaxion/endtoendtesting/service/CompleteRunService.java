package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportPipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.PipelineCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CompleteRunService {

  private final PipelineRetrievalService pipelineRetrievalService;

  private final Event<PipelineCompletedEvent> allTestsRunCompletedEvent;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(String pipelineId, ReportPipelineStatus reportPipelineStatus) {

    var pipeline = pipelineRetrievalService.get(pipelineId);

    pipeline.setStatus(
        reportPipelineStatus.equals(ReportPipelineStatus.CANCELED)
            ? PipelineStatus.CANCELED
            : PipelineStatus.FINISH);
    pipeline.setReportError(reportPipelineStatus.getErrorMessage());

    allTestsRunCompletedEvent.fire(
        PipelineCompletedEvent.builder()
            .environmentId(pipeline.getEnvironment().getId())
            .pipelineId(pipelineId)
            .build());

    cacheManager
        .getCache("in_progress_pipelines")
        .ifPresent(cache -> cache.invalidateAll().await().indefinitely());
  }
}
