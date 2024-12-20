package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import fr.plum.e2e.manager.core.domain.model.view.SearchCriteriaView;
import fr.plum.e2e.manager.core.domain.port.out.query.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.usecase.suite.GetSuiteSearchCriteriaUseCase;
import fr.plum.e2e.manager.core.domain.usecase.suite.SearchSuiteUseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SuiteFacade {

  private final GetSuiteSearchCriteriaUseCase getSuiteSearchCriteriaUseCase;
  private final SearchSuiteUseCase searchSuiteUseCase;

  public SuiteFacade(
      SearchSuiteConfigurationPort searchSuiteConfigurationPort,
      WorkerRepositoryPort workerRepositoryPort) {
    this.getSuiteSearchCriteriaUseCase =
        new GetSuiteSearchCriteriaUseCase(searchSuiteConfigurationPort);
    this.searchSuiteUseCase =
        new SearchSuiteUseCase(searchSuiteConfigurationPort, workerRepositoryPort);
  }

  public SearchCriteriaView getSearchCriteria(CommonQuery query) {
    return getSuiteSearchCriteriaUseCase.execute(query);
  }

  public PaginatedView<ConfigurationSuiteWithWorkerView> searchSuites(
      SearchSuiteConfigurationQuery query) {
    return searchSuiteUseCase.execute(query);
  }
}
