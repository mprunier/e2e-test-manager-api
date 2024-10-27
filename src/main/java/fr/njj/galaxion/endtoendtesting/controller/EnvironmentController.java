package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.SyncEnvironmentErrorResponse;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentDetailsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentErrorUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentsUseCase;
import io.quarkus.cache.CacheResult;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/environments")
@RequiredArgsConstructor
public class EnvironmentController {

  private final RetrieveEnvironmentDetailsUseCase retrieveEnvironmentDetailsUseCase;
  private final RetrieveEnvironmentsUseCase retrieveEnvironmentsUseCase;
  private final RetrieveEnvironmentErrorUseCase retrieveEnvironmentErrorUseCase;

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

  @GET
  @Path("{id}/errors")
  public List<SyncEnvironmentErrorResponse> retrieveErrors(@PathParam("id") Long id) {
    return retrieveEnvironmentErrorUseCase.execute(id);
  }
}
