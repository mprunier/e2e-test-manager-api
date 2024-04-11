package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentService;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheManager;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/environments")
@RequiredArgsConstructor
public class SecuredEnvironmentController {

    private final EnvironmentService environmentService;
    private final CacheManager cacheManager;

    @POST
    public EnvironmentResponse create(@Valid @RequestBody CreateUpdateEnvironmentRequest request) {
        var response = environmentService.create(request);
        cacheManager.getCache("environments").ifPresent(cache -> cache.invalidateAll().await().indefinitely());
        cacheManager.getCache("schedulers").ifPresent(cache -> cache.invalidateAll().await().indefinitely());
        return response;
    }

    @PUT
    @Path("{id}")
    public void update(@CacheKey @PathParam("id") Long id,
                       @Valid @RequestBody CreateUpdateEnvironmentRequest request) {
        environmentService.update(id, request);
    }

    @PATCH
    @Path("{id}")
    public void updateIsEnabled(@CacheKey @PathParam("id") Long id,
                                @NotNull @QueryParam("isEnabled") Boolean isEnabled) {
        environmentService.updateIsEnabled(id, isEnabled);
    }
}

