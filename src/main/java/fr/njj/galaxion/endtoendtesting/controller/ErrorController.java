package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.SyncEnvironmentErrorResponse;
import fr.njj.galaxion.endtoendtesting.usecases.environment.RetrieveEnvironmentErrorUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/errors")
@RequiredArgsConstructor
public class ErrorController {

  private final RetrieveEnvironmentErrorUseCase retrieveEnvironmentErrorUseCase;

  @GET
  public List<SyncEnvironmentErrorResponse> retrieveErrors(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveEnvironmentErrorUseCase.execute(environmentId);
  }
}
