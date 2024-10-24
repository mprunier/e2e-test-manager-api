package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.PipelineResponse;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RetrieveAllTestsPipelinesUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/pipelines")
@RequiredArgsConstructor
public class PipelineController {

  private final RetrieveAllTestsPipelinesUseCase retrieveAllTestsPipelinesUseCase;

  @GET
  @Path("/all-tests")
  public List<PipelineResponse> retrieveAllTestsPipelines(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveAllTestsPipelinesUseCase.execute(environmentId);
  }
}
