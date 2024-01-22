package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CompleteAllTestsRunService {

  private final EnvironmentRetrievalService environmentRetrievalService;

  private final Event<AllTestsRunCompletedEvent> allTestsRunCompletedEvent;

  @Transactional
  public void complete(long environmentId, ReportAllTestRanStatus reportAllTestRanStatus) {

    var entity = environmentRetrievalService.get(environmentId);
    entity.setIsRunningAllTests(false);
    if (reportAllTestRanStatus != null) {
      entity.setLastALlTestsError(reportAllTestRanStatus.getErrorMessage());
    } else {
      entity.setLastALlTestsError(null);
    }

    allTestsRunCompletedEvent.fire(
        AllTestsRunCompletedEvent.builder()
            .lastAllTestsError(entity.getLastALlTestsError())
            .environmentId(environmentId)
            .build());
  }
}
