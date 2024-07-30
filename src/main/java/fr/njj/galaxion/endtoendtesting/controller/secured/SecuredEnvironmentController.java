package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.usecases.environment.CreateEnvironmentUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.ToggleEnvironmentUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.UpdateEnvironmentUseCase;
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

    private final CreateEnvironmentUseCase createEnvironmentUseCase;
    private final UpdateEnvironmentUseCase updateEnvironmentUseCase;
    private final ToggleEnvironmentUseCase toggleEnvironmentUseCase;
    private final CacheManager cacheManager;

    @POST
    public EnvironmentResponse create(@Valid @RequestBody CreateUpdateEnvironmentRequest request) {
        var response = createEnvironmentUseCase.execute(request);
        cacheManager.getCache("environments").ifPresent(cache -> cache.invalidateAll().await().indefinitely());
        cacheManager.getCache("schedulers").ifPresent(cache -> cache.invalidateAll().await().indefinitely());
        return response;
    }

    @PUT
    @Path("{id}")
    public void update(@CacheKey @PathParam("id") Long id,
                       @Valid @RequestBody CreateUpdateEnvironmentRequest request) {
        updateEnvironmentUseCase.execute(id, request);
    }

    @PATCH
    @Path("{id}")
    public void updateIsEnabled(@CacheKey @PathParam("id") Long id,
                                @NotNull @QueryParam("isEnabled") Boolean isEnabled) {
        toggleEnvironmentUseCase.updateIsEnabled(id, isEnabled);
    }
}

