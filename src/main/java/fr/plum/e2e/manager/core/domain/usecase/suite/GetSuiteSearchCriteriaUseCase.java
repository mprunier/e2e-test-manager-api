package fr.plum.e2e.manager.core.domain.usecase.suite;

import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;
import fr.plum.e2e.manager.core.domain.port.out.query.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetSuiteSearchCriteriaUseCase
    implements QueryUseCase<CommonQuery, SearchCriteriaView> {

  private final SearchSuiteConfigurationPort searchSuiteConfigurationPort;

  public GetSuiteSearchCriteriaUseCase(SearchSuiteConfigurationPort searchSuiteConfigurationPort) {
    this.searchSuiteConfigurationPort = searchSuiteConfigurationPort;
  }

  @Override
  public SearchCriteriaView execute(CommonQuery query) {
    return searchSuiteConfigurationPort.findAllCriteria(query.environmentId());
  }
}
