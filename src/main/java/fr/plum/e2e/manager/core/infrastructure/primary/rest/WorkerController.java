package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;

import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.RunRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/workers")
@RequiredArgsConstructor
public class WorkerController {

  private final WorkerFacade workerFacade;

  private final SecurityIdentity identity;

  @POST
  public void run(
      @NotNull @QueryParam("environmentId") UUID environmentId, @RequestBody RunRequest request) {
    var username = extractUsername(identity);
    log.info("[{}] ran testFilter(s) on Environment id [{}].", username, environmentId);
    workerFacade.run(request.toCommand(environmentId, username));
  }

  @DELETE
  @Path("/{worker_id}/cancel")
  public void cancel(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @PathParam("worker_id") UUID workerId) {
    var username = extractUsername(identity);
    log.info("[{}] cancel testFilter(s) on Environment id [{}].", username, environmentId);
    workerFacade.cancel(
        new CancelWorkerCommand(
            new EnvironmentId(environmentId),
            new ActionUsername(username),
            new WorkerId(workerId)));
  }

  @GET
  @Path("/type-all")
  public List<WorkerUnitResponse> get(@NotNull @QueryParam("environmentId") UUID environmentId) {
    var optionalWorker = workerFacade.get(new CommonQuery(new EnvironmentId(environmentId)));
    if (optionalWorker.isPresent()) {
      return WorkerUnitResponse.fromWorkers(optionalWorker.get().getWorkerUnits());
    }
    return List.of();
  }
}
