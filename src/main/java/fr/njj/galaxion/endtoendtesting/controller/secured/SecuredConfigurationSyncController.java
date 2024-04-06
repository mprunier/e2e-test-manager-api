package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.usecases.cache.CleanCacheByEnvironmentUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Authenticated
@Path("/auth/configurations/synchronizations")
@RequiredArgsConstructor
public class SecuredConfigurationSyncController {

    private final ConcurrentMap<String, Boolean> locks = new ConcurrentHashMap<>();

    private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;
    private final CleanCacheByEnvironmentUseCase cleanCacheByEnvironmentUseCase;

    @POST
    public void synchronize(@NotNull @QueryParam("environmentId") Long environmentId) {
        if (locks.putIfAbsent(environmentId.toString(), true) != null) {
            log.info("Global synchronization is already in progress for Environment ID [{}].", environmentId);
            return;
        }
        try {
            log.info("Start Global synchronization for Environment ID [{}].", environmentId);
            CompletableFuture.runAsync(() -> {
                globalEnvironmentSynchronizationUseCase.execute(environmentId);
                cleanCacheByEnvironmentUseCase.execute(environmentId);
            });
        } finally {
            locks.remove(environmentId.toString());
        }
    }
}

