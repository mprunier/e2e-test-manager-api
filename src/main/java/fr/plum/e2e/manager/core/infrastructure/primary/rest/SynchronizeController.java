package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;

import fr.plum.e2e.manager.core.application.SynchronizationFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SynchronizationErrorResponse;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
@Path("/auth/synchronize")
@RequiredArgsConstructor
public class SynchronizeController {

  private final SynchronizationFacade synchronizationFacade;

  private final SecurityIdentity identity;

  @POST
  public void synchronize(@NotNull @QueryParam("environmentId") UUID environmentId) {
    var username = extractUsername(identity);
    log.info("[{}] ran synchronization on Environment id [{}].", username, environmentId);
    var command =
        CommonCommand.builder()
            .environmentId(new EnvironmentId(environmentId))
            .username(new ActionUsername(username))
            .build();
    synchronizationFacade.startSynchronization(command);
  }

  @GET
  @Path("/errors")
  public List<SynchronizationErrorResponse> retrieveErrors(
      @NotNull @QueryParam("environmentId") UUID environmentId) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var synchronizationErrors = synchronizationFacade.listErrors(query);
    return SynchronizationErrorResponse.fromDomain(synchronizationErrors);
  }
}
