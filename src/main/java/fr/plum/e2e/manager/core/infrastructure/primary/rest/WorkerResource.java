package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;

import fr.plum.e2e.manager.core.application.command.worker.CancelWorkerCommandHandler;
import fr.plum.e2e.manager.core.application.command.worker.RunWorkerCommandHandler;
import fr.plum.e2e.manager.core.application.query.worker.GetTypeAllWorkerQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.RunRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "WorkerApi")
@Slf4j
@Authenticated
@Path("/auth/workers")
@RequiredArgsConstructor
public class WorkerResource {

  private final RunWorkerCommandHandler runWorkerCommandHandler;
  private final CancelWorkerCommandHandler cancelWorkerCommandHandler;
  private final GetTypeAllWorkerQueryHandler getTypeAllWorkerQueryHandler;

  private final SecurityIdentity identity;

  @Operation(operationId = "run")
  @POST
  public void run(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @Nullable @RequestBody RunRequest request) {
    var username = extractUsername(identity);
    if (request == null) {
      runWorkerCommandHandler.execute(
          RunWorkerCommand.builder()
              .environmentId(new EnvironmentId(environmentId))
              .username(new ActionUsername(username))
              .build());
    } else {
      runWorkerCommandHandler.execute(request.toCommand(environmentId, username));
    }
  }

  @Operation(operationId = "cancel")
  @DELETE
  @Path("/{worker_id}/cancel")
  public void cancel(@PathParam("worker_id") UUID workerId) {
    var username = extractUsername(identity);
    cancelWorkerCommandHandler.execute(
        new CancelWorkerCommand(new ActionUsername(username), new WorkerId(workerId)));
  }

  @Operation(operationId = "getTypeAllWorkerUnits")
  @GET
  @Path("/units/type-all")
  public List<WorkerUnitResponse> getTypeAllWorkerUnits(
      @NotNull @QueryParam("environmentId") UUID environmentId) {
    var optionalWorker =
        getTypeAllWorkerQueryHandler.execute(new CommonQuery(new EnvironmentId(environmentId)));
    if (optionalWorker.isPresent()) {
      return WorkerUnitResponse.fromWorkers(optionalWorker.get().getWorkerUnits());
    }
    return new ArrayList<>();
  }
}
