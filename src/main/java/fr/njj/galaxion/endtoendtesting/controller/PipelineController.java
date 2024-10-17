package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.AllTestsPipelineStatusResponse;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RetrieveAllTestsPipelineStatusUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/pipelines")
@RequiredArgsConstructor
public class PipelineController {

  private final RetrieveAllTestsPipelineStatusUseCase retrieveAllTestsPipelineStatusUseCase;

  @GET
  @Path("/all-tests/status")
  public AllTestsPipelineStatusResponse retrievePipelines(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveAllTestsPipelineStatusUseCase.execute(environmentId);
  }
}
