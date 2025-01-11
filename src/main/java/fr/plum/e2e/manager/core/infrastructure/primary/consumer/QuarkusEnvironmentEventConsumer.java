package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_ENVIRONMENT_DETAILS;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_LIST_ALL_ENVIRONMENTS;

import fr.plum.e2e.manager.core.domain.model.event.EnvironmentCreatedEvent;
import fr.plum.e2e.manager.core.domain.model.event.EnvironmentUpdatedEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.scheduler.RunWorkerScheduler;
import fr.plum.e2e.manager.core.infrastructure.secondary.cache.QuarkusCacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusEnvironmentEventConsumer {

  private final QuarkusCacheManager cacheManager;

  private final RunWorkerScheduler runWorkerScheduler;

  public void environmentCreated(@ObservesAsync EnvironmentCreatedEvent event) {
    try {
      runWorkerScheduler.updateSchedule();
      cacheManager.invalidateCache(CACHE_HTTP_LIST_ALL_ENVIRONMENTS);
    } catch (Exception e) {
      log.error("Error while updating scheduler", e);
    }
  }

  public void environmentUpdated(@ObservesAsync EnvironmentUpdatedEvent event) {
    cacheManager.invalidateCache(CACHE_HTTP_LIST_ALL_ENVIRONMENTS);
    cacheManager.invalidateCacheByKey(
        CACHE_HTTP_GET_ENVIRONMENT_DETAILS, event.environmentId().value());
  }
}
