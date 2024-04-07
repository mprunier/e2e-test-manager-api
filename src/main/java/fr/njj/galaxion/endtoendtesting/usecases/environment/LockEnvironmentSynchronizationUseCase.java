package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class LockEnvironmentSynchronizationUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final CacheManager cacheManager;
    
    @Transactional
    public void execute(
            long environmentId) {

        var environment = environmentRetrievalService.getEnvironment(environmentId);
        environment.setIsLocked(true);
        cacheManager.getCache("environment").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
    }

}

