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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "TestResultApi")
@Slf4j
@Authenticated
@Path("/auth/test-results")
@RequiredArgsConstructor
public class TestResultResource {

  private final TestResultFacade testResultFacade;

  @Operation(operationId = "getAll")
  @GET
  public List<TestResultResponse> getAllTestResult(
      @NotNull @QueryParam("testConfigurationId") UUID testConfigurationId) {
    var query = new GetAllTestResultQuery(new TestConfigurationId(testConfigurationId));
    return TestResultResponse.fromDomain(testResultFacade.getAllTestResult(query));
  }

  @Operation(operationId = "getErrorDetails")
  @GET
  @Path("/{id}/error-details")
  public TestResultErrorDetailsResponse getTestResultErrorDetails(
      @NotNull @PathParam("id") UUID testResultId) {
    var query = new GetTestResultErrorDetailsQuery(new TestResultId(testResultId));
    return TestResultErrorDetailsResponse.fromDomain(
        testResultFacade.getTestResultErrorDetails(query));
  }

  @Operation(operationId = "downloadVideo")
  @GET
  @Path("/medias/videos/{id}")
  @Produces("video/mp4")
  public byte[] downloadVideo(@PathParam("id") UUID id) {
    return testResultFacade.downloadVideo(new DownloadVideoQuery(new TestResultVideoId(id)));
  }

  @Operation(operationId = "downloadScreenshot")
  @GET
  @Path("/medias/screenshots/{id}")
  @Produces("image/png")
  public byte[] downloadScreenshot(@PathParam("id") UUID id) {
    return testResultFacade.downloadScreenshot(
        new DownloadScreenshotQuery(new TestResultScreenshotId(id)));
  }
}
