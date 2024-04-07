package fr.njj.galaxion.endtoendtesting.usecases.cache;

import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanCacheAfterSynchronizationUseCase {

    private final CacheManager cacheManager;

    public void execute(
            long environmentId) {

        cacheManager.getCache("suites").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
        cacheManager.getCache("tests").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
        cacheManager.getCache("files").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
        cacheManager.getCache("identifiers").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
    }

}

