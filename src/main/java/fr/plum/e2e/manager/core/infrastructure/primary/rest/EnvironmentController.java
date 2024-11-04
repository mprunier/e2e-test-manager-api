package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_ENVIRONMENT_DETAILS;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_LIST_ALL_ENVIRONMENTS;

import fr.plum.e2e.manager.core.application.EnvironmentFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.CreateUpdateEnvironmentRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.EnvironmentDetailsResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.EnvironmentResponse;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/environments")
@RequiredArgsConstructor
public class EnvironmentController {

  private final EnvironmentFacade environmentFacade;

  private final SecurityIdentity identity;

  @CacheResult(cacheName = CACHE_HTTP_LIST_ALL_ENVIRONMENTS)
  @GET
  public List<EnvironmentResponse> listAll() {
    return EnvironmentResponse.from(environmentFacade.listAllEnvironments());
  }

  @CacheResult(cacheName = CACHE_HTTP_GET_ENVIRONMENT_DETAILS)
  @GET
  @Path("/{id}")
  public EnvironmentDetailsResponse get(@CacheKey @PathParam("id") UUID id) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(id)).build();
    var environment = environmentFacade.getEnvironmentDetails(query);
    return EnvironmentDetailsResponse.from(environment);
  }

  @POST
  public void create(@Valid @RequestBody CreateUpdateEnvironmentRequest request) {
    var username = extractUsername(identity);
    log.info("[{}] created a new environment [{}].", username, request.description());
    var command = request.toCommand(null, username);
    environmentFacade.createEnvironment(command);
  }

  @PUT
  @Path("/{id}")
  public void update(
      @PathParam("id") UUID environmentId,
      @Valid @RequestBody CreateUpdateEnvironmentRequest request) {
    var username = extractUsername(identity);
    log.info("[{}] updated environment id [{}].", username, environmentId);
    var command = request.toCommand(environmentId, username);
    environmentFacade.updateEnvironment(command);
  }
}
