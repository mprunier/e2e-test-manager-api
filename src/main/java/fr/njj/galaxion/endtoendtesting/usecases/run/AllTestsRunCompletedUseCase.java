package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AllTestsRunCompletedUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final Event<AllTestsRunCompletedEvent> allTestsRunCompletedEvent;

    @Monitored(logExit = false)
    @Transactional
    public void execute(
            long environmentId,
            ReportAllTestRanStatus reportAllTestRanStatus) {

        var entity = environmentRetrievalService.getEnvironment(environmentId);
        entity.setIsRunningAllTests(false);
        if (reportAllTestRanStatus != null) {
            entity.setLastALlTestsError(reportAllTestRanStatus.getErrorMessage());
        } else {
            entity.setLastALlTestsError(null);
        }

        allTestsRunCompletedEvent.fire(AllTestsRunCompletedEvent.builder().lastAllTestsError(entity.getLastALlTestsError()).environmentId(environmentId).build());
    }

}

