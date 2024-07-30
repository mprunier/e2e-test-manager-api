package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.request.UpdateConfigurationSchedulerRequest;
import fr.njj.galaxion.endtoendtesting.usecases.flowscheduler.UpdateFlowSchedulerUseCase;
import io.quarkus.cache.CacheManager;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/configurations/schedulers")
@RequiredArgsConstructor
public class SecuredConfigurationSchedulerController {

    private final UpdateFlowSchedulerUseCase updateFlowSchedulerUseCase;
    private final CacheManager cacheManager;

    @PUT
    public void update(@NotNull @QueryParam("environmentId") Long environmentId,
                       @RequestBody UpdateConfigurationSchedulerRequest request) {
        updateFlowSchedulerUseCase.execute(environmentId, request);
        cacheManager.getCache("schedulers").ifPresent(cache -> cache.invalidateAll().await().indefinitely());
    }
}

