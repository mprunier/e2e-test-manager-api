package fr.plum.e2e.manager.core.application.query.suite;

import fr.plum.e2e.manager.core.domain.model.projection.SearchCriteriaProjection;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.view.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetSuiteSearchCriteriaQueryHandler
    implements QueryHandler<CommonQuery, SearchCriteriaProjection> {

  private final SearchSuiteConfigurationPort searchSuiteConfigurationPort;

  public GetSuiteSearchCriteriaQueryHandler(
      SearchSuiteConfigurationPort searchSuiteConfigurationPort) {
    this.searchSuiteConfigurationPort = searchSuiteConfigurationPort;
  }

  @Override
  public SearchCriteriaProjection execute(CommonQuery query) {
    return searchSuiteConfigurationPort.findAllCriteria(query.environmentId());
  }
}
