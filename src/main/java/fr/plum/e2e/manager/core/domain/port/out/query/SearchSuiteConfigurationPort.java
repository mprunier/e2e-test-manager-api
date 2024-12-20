package fr.plum.e2e.manager.core.domain.port.out.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteView;
import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;

public interface SearchSuiteConfigurationPort {

  PaginatedView<ConfigurationSuiteView> search(SearchSuiteConfigurationQuery query);

  SearchCriteriaView findAllCriteria(EnvironmentId environmentId);
}
