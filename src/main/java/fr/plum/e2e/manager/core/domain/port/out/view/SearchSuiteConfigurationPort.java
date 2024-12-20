package fr.plum.e2e.manager.core.domain.port.out.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteProjection;
import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import fr.plum.e2e.manager.core.domain.model.projection.SearchCriteriaProjection;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;

public interface SearchSuiteConfigurationPort {

  PaginatedProjection<ConfigurationSuiteProjection> search(SearchSuiteConfigurationQuery query);

  SearchCriteriaProjection findAllCriteria(EnvironmentId environmentId);
}
