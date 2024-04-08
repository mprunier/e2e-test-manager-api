package fr.njj.galaxion.endtoendtesting.usecases.scheduler;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateSchedulerStatusUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CacheManager cacheManager;

    @Monitored
    @Transactional
    public void execute(
            long environmentId,
            SchedulerStatus status) {

        var entity = environmentRetrievalService.getEnvironment(environmentId);
        entity.setSchedulerStatus(status);

        cacheManager.getCache("environment").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
    }

}

