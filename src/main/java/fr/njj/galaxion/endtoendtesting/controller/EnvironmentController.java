package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentErrorResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentErrorUseCase;
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

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final RetrieveEnvironmentErrorUseCase retrieveEnvironmentErrorUseCase;

    @GET
    @Path("{id}")
    @CacheResult(cacheName = "environment")
    public EnvironmentResponse getEnvironmentResponse(@PathParam("id") Long id) {
        return environmentRetrievalService.getEnvironmentResponse(id);
    }

    @GET
    @CacheResult(cacheName = "environments")
    public List<EnvironmentResponse> getEnvironments() {
        return environmentRetrievalService.getEnvironmentResponses();
    }

    @GET
    @Path("{id}/errors")
    public List<EnvironmentErrorResponse> retrieve(@PathParam("id") Long id) {
        return retrieveEnvironmentErrorUseCase.execute(id);
    }
}

