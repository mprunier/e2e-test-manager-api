package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_SCHEDULER_DETAILS;

import fr.plum.e2e.manager.core.application.SchedulerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.UpdateSchedulerRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SchedulerResponse;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "SchedulerApi")
@Slf4j
@Authenticated
@Path("/auth/schedulers")
@RequiredArgsConstructor
public class SchedulerResource {

  private final SchedulerFacade schedulerFacade;

  private final SecurityIdentity identity;

  @Operation(operationId = "getScheduler")
  @GET
  @CacheResult(cacheName = CACHE_HTTP_GET_SCHEDULER_DETAILS)
  public SchedulerResponse retrieve(
      @CacheKey @NotNull @QueryParam("environmentId") UUID environmentId) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var scheduler = schedulerFacade.getSchedulerDetails(query);
    return SchedulerResponse.fromDomain(scheduler);
  }

  @Operation(operationId = "updateScheduler")
  @PUT
  public void update(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @RequestBody UpdateSchedulerRequest request) {
    var username = extractUsername(identity);
    log.info("[{}] updated scheduler on Environment id [{}].", username, environmentId);
    schedulerFacade.updateScheduler(request.toCommand(environmentId, username));
  }
}
