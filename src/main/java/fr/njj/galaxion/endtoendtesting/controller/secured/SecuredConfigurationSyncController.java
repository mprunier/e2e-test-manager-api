package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.usecases.environment.LockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
@Path("/auth/configurations/synchronizations")
@RequiredArgsConstructor
public class SecuredConfigurationSyncController {

  private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;
  private final LockEnvironmentSynchronizationUseCase lockEnvironmentSynchronizationUseCase;
  private final SecurityIdentity identity;

  @POST
  public void synchronize(@NotNull @QueryParam("environmentId") Long environmentId) {
    var createdBy =
        identity != null && identity.getPrincipal() != null
            ? identity.getPrincipal().getName()
            : "Unknown";
    log.info("[{}] ran synchronization on Environment id [{}].", createdBy, environmentId);
    lockEnvironmentSynchronizationUseCase.execute(environmentId);
    CompletableFuture.runAsync(
        () -> {
          try {
            globalEnvironmentSynchronizationUseCase.execute(environmentId);
          } catch (Exception exception) {
            log.error("Synchronize Exception", exception);
          }
        });
  }
}
