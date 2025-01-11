package fr.plum.e2e.manager.core.infrastructure.secondary.cache;

import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusCacheManager {
  private final CacheManager cacheManager;

  public void invalidateCache(String name) {
    cacheManager.getCache(name).ifPresent(cache -> cache.invalidateAll().await().indefinitely());
  }

  public void invalidateCacheByKey(String name, Object key) {
    cacheManager.getCache(name).ifPresent(cache -> cache.invalidate(key).await().indefinitely());
  }
}
