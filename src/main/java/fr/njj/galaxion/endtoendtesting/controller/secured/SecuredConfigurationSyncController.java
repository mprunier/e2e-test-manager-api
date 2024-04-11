package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.usecases.environment.LockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.UnLockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.websocket.events.SyncErrorEventService;
import fr.njj.galaxion.endtoendtesting.websocket.events.UpdateEnvironmentEventService;
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

    private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;
    private final LockEnvironmentSynchronizationUseCase lockEnvironmentSynchronizationUseCase;
    private final UnLockEnvironmentSynchronizationUseCase unLockEnvironmentSynchronizationUseCase;
    private final SyncErrorEventService syncErrorEventService;
    private final UpdateEnvironmentEventService updateEnvironmentEventService;

    @POST
    public void synchronize(@NotNull @QueryParam("environmentId") Long environmentId) {
        lockEnvironmentSynchronizationUseCase.execute(environmentId);
        CompletableFuture.runAsync(() -> {
            try {
                globalEnvironmentSynchronizationUseCase.execute(environmentId);
            } catch (Exception exception) {
                log.error("Synchronize Exception", exception);
            } finally {
                unLockEnvironmentSynchronizationUseCase.execute(environmentId);
                updateEnvironmentEventService.send(environmentId);
                syncErrorEventService.send(environmentId);
            }
        });
    }
}

