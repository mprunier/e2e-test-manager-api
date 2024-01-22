package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.SynchronizationStatus;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSynchronizationService;
import io.quarkus.cache.CacheManager;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Authenticated
@Path("/auth/configurations/synchronizations")
@RequiredArgsConstructor
public class SecuredConfigurationSyncController {

    private final ConfigurationSynchronizationService configurationSynchronizationService;
    private final CacheManager cacheManager;

    @POST
    public void synchronize(@NotNull @QueryParam("environmentId") Long environmentId) {
        configurationSynchronizationService.assertEnvironmentIsNotInSync(environmentId);
        configurationSynchronizationService.updateSync(environmentId, SynchronizationStatus.IN_PROGRESS, null);
        CompletableFuture.runAsync(() -> {
            try {
                configurationSynchronizationService.synchronize(environmentId);
                cacheManager.getCache("suites").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
                cacheManager.getCache("tests").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
                cacheManager.getCache("files").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
                cacheManager.getCache("identifiers").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());
            } catch (CustomException e) {
                configurationSynchronizationService.updateSync(environmentId, SynchronizationStatus.FAILED, e.getDetail());
                log.error("CustomException : ", e);
                throw new RuntimeException(e);
            } catch (Exception e) {
                configurationSynchronizationService.updateSync(environmentId, SynchronizationStatus.FAILED, e.getMessage());
                log.error("Exception : ", e);
                throw new RuntimeException(e);
            }
        });
    }
}

