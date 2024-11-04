package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_SCHEDULER_DETAILS;

import fr.plum.e2e.manager.core.application.SchedulerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.UpdateSchedulerRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SchedulerResponse;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Path("/auth/schedulers")
@RequiredArgsConstructor
public class SchedulerController {

  private final SchedulerFacade schedulerFacade;

  @GET
  @CacheResult(cacheName = CACHE_HTTP_GET_SCHEDULER_DETAILS)
  public SchedulerResponse retrieve(
      @CacheKey @NotNull @QueryParam("environmentId") UUID environmentId) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var scheduler = schedulerFacade.getSchedulerDetails(query);
    return SchedulerResponse.fromDomain(scheduler);
  }

  @PUT
  public void update(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @RequestBody UpdateSchedulerRequest request) {
    schedulerFacade.updateScheduler(request.toCommand(environmentId));
  }
}
