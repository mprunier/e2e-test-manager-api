package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.SuiteFacade;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.SearchSuiteConfigurationRequest;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.ConfigurationSuiteWithWorkerResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.PaginatedResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SearchCriteriaResponse;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "SuiteApi")
@Slf4j
@Authenticated
@Path("/auth/suites/search")
@RequiredArgsConstructor
public class SuiteResource {

  private final SuiteFacade suiteFacade;

  @Operation(operationId = "searchSuites")
  @GET
  public PaginatedResponse<ConfigurationSuiteWithWorkerResponse> searchSuites(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @Valid @BeanParam SearchSuiteConfigurationRequest request) {
    return PaginatedResponse.fromDomain(
        suiteFacade.searchSuites(request.toQuery(environmentId)),
        ConfigurationSuiteWithWorkerResponse::fromDomain);
  }

  @Operation(operationId = "getSearchCriteria")
  @GET
  @Path("/criteria")
  public SearchCriteriaResponse getSearchCriteria(
      @NotNull @QueryParam("environmentId") UUID environmentId) {
    return SearchCriteriaResponse.fromDomain(
        suiteFacade.getSearchCriteria(CommonQuery.fromEnvironmentUUID(environmentId)));
  }
}
