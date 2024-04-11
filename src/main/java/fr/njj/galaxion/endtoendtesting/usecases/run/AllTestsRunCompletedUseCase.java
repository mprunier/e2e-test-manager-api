package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.websocket.events.AllTestsRunCompletedEventService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AllTestsRunCompletedUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CacheManager cacheManager;
    private final AllTestsRunCompletedEventService allTestsRunCompletedEventService;

    @Monitored
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

        allTestsRunCompletedEventService.send(environmentId, entity.getLastALlTestsError());

        cacheManager.getCache("environment").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
    }

}

