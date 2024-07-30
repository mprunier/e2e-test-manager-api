package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentDetailsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentsUseCase;
import io.quarkus.cache.CacheResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

    private final RetrieveEnvironmentDetailsUseCase retrieveEnvironmentDetailsUseCase;
    private final RetrieveEnvironmentsUseCase retrieveEnvironmentsUseCase;

    @GET
    @Path("{id}")
    public EnvironmentResponse getEnvironmentResponse(@PathParam("id") Long id) {
        return retrieveEnvironmentDetailsUseCase.execute(id);
    }

    @GET
    @CacheResult(cacheName = "environments")
    public List<EnvironmentResponse> getEnvironments() {
        return retrieveEnvironmentsUseCase.execute();
    }
}

