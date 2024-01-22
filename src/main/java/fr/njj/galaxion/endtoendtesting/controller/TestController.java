package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.ScreenshotResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.TestResponse;
import fr.njj.galaxion.endtoendtesting.service.retrieval.TestRetrievalService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/tests")
@RequiredArgsConstructor
public class TestController {

  private final TestRetrievalService testRetrievalService;

  @GET
  public List<TestResponse> getResponses(
      @QueryParam("configurationTestId") Long configurationTestId) {
    return testRetrievalService.getResponses(configurationTestId);
  }

  @GET
  @Path("/errors")
  public List<TestResponse> getErrorResponses(@QueryParam("pipelineId") String pipelineId) {
    return testRetrievalService.getErrorResponses(pipelineId);
  }

  @GET
  @Path("{id}")
  public TestResponse getResponse(@PathParam("id") Long id) {
    return testRetrievalService.getResponse(id);
  }

  @GET
  @Path("{id}/screenshots")
  public List<ScreenshotResponse> getScreenshots(@PathParam("id") Long id) {
    return testRetrievalService.getScreenshots(id);
  }
}
