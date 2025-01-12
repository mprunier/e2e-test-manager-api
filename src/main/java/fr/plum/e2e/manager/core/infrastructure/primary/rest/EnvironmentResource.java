package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import static fr.plum.e2e.manager.core.infrastructure.primary.rest.utils.RestUtils.extractUsername;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_GET_ENVIRONMENT_DETAILS;
import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_HTTP_LIST_ALL_ENVIRONMENTS;

import fr.plum.e2e.manager.core.application.command.environment.CreateEnvironmentCommandHandler;
import fr.plum.e2e.manager.core.application.command.environment.UpdateEnvironmentCommandHandler;
import fr.plum.e2e.manager.core.application.query.environment.GetEnvironmentDetailsQueryHandler;
import fr.plum.e2e.manager.core.application.query.environment.ListAllEnvironmentsQueryHandler;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "EnvironmentApi")
@Slf4j
@Authenticated
@Path("/auth/environments")
@RequiredArgsConstructor
public class EnvironmentResource {

  private final CreateEnvironmentCommandHandler createEnvironmentCommandHandler;
  private final UpdateEnvironmentCommandHandler updateEnvironmentCommandHandler;

  private final GetEnvironmentDetailsQueryHandler getEnvironmentDetailsQueryHandler;
  private final ListAllEnvironmentsQueryHandler listAllEnvironmentsQueryHandler;

  private final SecurityIdentity identity;

  @Operation(operationId = "listAllEnvironments")
  @CacheResult(cacheName = CACHE_HTTP_LIST_ALL_ENVIRONMENTS)
  @GET
  public List<EnvironmentResponse> listAll() {
    return EnvironmentResponse.from(listAllEnvironmentsQueryHandler.execute());
  }

  @Operation(operationId = "getEnvironment")
  @CacheResult(cacheName = CACHE_HTTP_GET_ENVIRONMENT_DETAILS)
  @GET
  @Path("/{id}")
  public EnvironmentDetailsResponse get(@CacheKey @PathParam("id") UUID id) {
    var query = CommonQuery.builder().environmentId(new EnvironmentId(id)).build();
    var environment = getEnvironmentDetailsQueryHandler.execute(query);
    return EnvironmentDetailsResponse.from(environment);
  }

  @Operation(operationId = "createEnvironment")
  @POST
  public void create(@Valid @RequestBody CreateUpdateEnvironmentRequest request) {
    var username = extractUsername(identity);
    var command = request.toCommand(username);
    createEnvironmentCommandHandler.execute(command);
  }

  @Operation(operationId = "updateEnvironment")
  @PUT
  @Path("/{id}")
  public void update(
      @PathParam("id") UUID environmentId,
      @Valid @RequestBody CreateUpdateEnvironmentRequest request) {
    var username = extractUsername(identity);
    var command = request.toCommand(environmentId, username);
    updateEnvironmentCommandHandler.execute(command);
  }
}
