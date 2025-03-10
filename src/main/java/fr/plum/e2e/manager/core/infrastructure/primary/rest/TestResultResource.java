package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.query.testresult.GetAllTestResultQueryHandler;
import fr.plum.e2e.manager.core.application.query.testresult.GetTestResultErrorDetailsQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailsQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.TestResultErrorDetailsResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.TestResultResponse;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

  private final GetAllTestResultQueryHandler getAllTestResultQueryHandler;
  private final GetTestResultErrorDetailsQueryHandler getTestResultErrorDetailsQueryHandler;

  @Operation(operationId = "getAllTestResult")
  @GET
  public List<TestResultResponse> getAllTestResult(
      @NotNull @QueryParam("testConfigurationId") UUID testConfigurationId) {
    var query = new GetAllTestResultQuery(new TestConfigurationId(testConfigurationId));
    return TestResultResponse.fromDomain(getAllTestResultQueryHandler.execute(query));
  }

  @Operation(operationId = "getErrorDetails")
  @GET
  @Path("/{id}/error-details")
  public TestResultErrorDetailsResponse getTestResultErrorDetails(
      @NotNull @PathParam("id") UUID testResultId) {
    var query = new GetTestResultErrorDetailsQuery(new TestResultId(testResultId));
    return TestResultErrorDetailsResponse.fromDomain(
        getTestResultErrorDetailsQueryHandler.execute(query));
  }
}
