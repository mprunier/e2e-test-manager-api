package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.SuiteFacade;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request.SearchSuiteConfigurationRequest;
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

@Slf4j
@Authenticated
@Path("/auth/suites/search")
@RequiredArgsConstructor
public class SuiteController {

  private final SuiteFacade suiteFacade;

  @GET
  public PaginatedView<ConfigurationSuiteWithWorkerView> searchSuites(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @Valid @BeanParam SearchSuiteConfigurationRequest request) {
    return suiteFacade.searchSuites(request.toQuery(environmentId));
  }

  @GET
  @Path("/criteria")
  public SearchCriteriaView getSearchCriteria(
      @NotNull @QueryParam("environmentId") UUID environmentId) {
    return suiteFacade.getSearchCriteria(CommonQuery.fromEnvironmentUUID(environmentId));
  }
}
