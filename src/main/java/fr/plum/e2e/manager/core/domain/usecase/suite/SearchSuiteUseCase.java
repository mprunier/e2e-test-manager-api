package fr.plum.e2e.manager.core.domain.usecase.suite;

import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import fr.plum.e2e.manager.core.domain.model.view.PaginatedView;
import fr.plum.e2e.manager.core.domain.port.out.query.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class SearchSuiteUseCase
    implements QueryUseCase<
        SearchSuiteConfigurationQuery, PaginatedView<ConfigurationSuiteWithWorkerView>> {

  private final SearchSuiteConfigurationPort searchSuiteConfigurationPort;
  private final WorkerRepositoryPort workerRepositoryPort;

  public SearchSuiteUseCase(
      SearchSuiteConfigurationPort searchSuiteConfigurationPort,
      WorkerRepositoryPort workerRepositoryPort) {
    this.searchSuiteConfigurationPort = searchSuiteConfigurationPort;
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public PaginatedView<ConfigurationSuiteWithWorkerView> execute(
      SearchSuiteConfigurationQuery query) {
    var paginatedSuites = searchSuiteConfigurationPort.search(query);
    var workers = workerRepositoryPort.findAll(query.environmentId());

    var suiteWithWorkers =
        paginatedSuites.getContent().stream()
            .map(suite -> ConfigurationSuiteWithWorkerView.from(suite, workers))
            .toList();

    return new PaginatedView<>(
        suiteWithWorkers,
        paginatedSuites.getCurrentPage(),
        paginatedSuites.getTotalPages(),
        paginatedSuites.getSize(),
        paginatedSuites.getTotalElements());
  }
}
