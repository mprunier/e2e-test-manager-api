package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_SCHEDULER_DETAILS;

import fr.plum.e2e.manager.core.domain.model.event.SchedulerUpdatedEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.cache.QuarkusCacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusSchedulerEventConsumer {

  private final QuarkusCacheManager cacheManager;

  public void schedulerUpdated(@ObservesAsync SchedulerUpdatedEvent event) {
    cacheManager.invalidateCacheByKey(
        CACHE_HTTP_GET_SCHEDULER_DETAILS, event.environmentId().value());
  }
}
