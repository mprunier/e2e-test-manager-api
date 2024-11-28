package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.TestResultFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.query.DownloadScreenshotQuery;
import fr.plum.e2e.manager.core.domain.model.query.DownloadVideoQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailsQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.TestResultErrorDetailsResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.TestResultResponse;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
@Path("/auth/test-results")
@RequiredArgsConstructor
public class TestResultController {

  private final TestResultFacade testResultFacade;

  @GET
  public List<TestResultResponse> getAllTestResult(
      @NotNull @QueryParam("testConfigurationId") UUID testConfigurationId) {
    var query = new GetAllTestResultQuery(new TestConfigurationId(testConfigurationId));
    return TestResultResponse.fromTestResultViews(testResultFacade.getAllTestResult(query));
  }

  @GET
  @Path("/{id}/error-details")
  public TestResultErrorDetailsResponse getTestResultErrorDetails(
      @NotNull @PathParam("id") UUID testResultId) {
    var query = new GetTestResultErrorDetailsQuery(new TestResultId(testResultId));
    return TestResultErrorDetailsResponse.fromTestResultErrorDetailView(
        testResultFacade.getTestResultErrorDetails(query));
  }

  @GET
  @Path("/medias/videos/{id}")
  @Produces("video/mp4")
  public byte[] downloadVideo(@PathParam("id") UUID id) {
    return testResultFacade.downloadVideo(new DownloadVideoQuery(new TestResultVideoId(id)));
  }

  @GET
  @Path("/medias/screenshots/{id}")
  @Produces("image/png")
  public byte[] downloadScreenshot(@PathParam("id") UUID id) {
    return testResultFacade.downloadScreenshot(
        new DownloadScreenshotQuery(new TestResultScreenshotId(id)));
  }
}
