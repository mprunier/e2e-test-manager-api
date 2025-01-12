package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;

import fr.plum.e2e.manager.core.application.command.synchronization.StartSynchronizationCommandHandler;
import fr.plum.e2e.manager.core.application.query.synchronization.ListAllSynchronizationErrorsQueryHandler;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "SynchronizeApi")
@Slf4j
@Authenticated
@Path("/auth/synchronize")
@RequiredArgsConstructor
public class SynchronizeResource {

  private final StartSynchronizationCommandHandler startSynchronizationCommandHandler;
  private final ListAllSynchronizationErrorsQueryHandler listAllSynchronizationErrorsQueryHandler;

  private final SecurityIdentity identity;

  @Operation(operationId = "synchronize")
  @POST
  public void synchronize(@NotNull @QueryParam("environmentId") UUID environmentId) {
    var username = extractUsername(identity);
    var command =
        CommonCommand.builder()
            .environmentId(new EnvironmentId(environmentId))
            .username(new ActionUsername(username))
            .build();
    startSynchronizationCommandHandler.execute(command);
  }

  @Operation(operationId = "getSynchronizeErrors")
  @GET
  @Path("/errors")
  public List<SynchronizationErrorResponse> retrieveErrors(
      @NotNull @QueryParam("environmentId") UUID environmentId) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var synchronizationErrors = listAllSynchronizationErrorsQueryHandler.execute(query);
    return SynchronizationErrorResponse.fromDomain(synchronizationErrors);
  }
}
