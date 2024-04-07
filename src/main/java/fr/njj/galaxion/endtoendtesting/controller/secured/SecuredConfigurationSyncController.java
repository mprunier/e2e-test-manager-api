package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.usecases.environment.LockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
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

    @POST
    public void synchronize(@NotNull @QueryParam("environmentId") Long environmentId) {
        lockEnvironmentSynchronizationUseCase.execute(environmentId);
        CompletableFuture.runAsync(() -> {
            try {
                globalEnvironmentSynchronizationUseCase.execute(environmentId);
            } finally {
                lockEnvironmentSynchronizationUseCase.execute(environmentId);
            }
        });

    }
}

