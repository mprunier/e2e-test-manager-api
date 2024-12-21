package fr.plum.e2e.manager.core.application.query.suite;

import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteWithWorkerProjection;
import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.port.repository.WorkerRepositoryPort;
import fr.plum.e2e.manager.core.domain.port.view.SearchSuiteConfigurationPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SearchSuiteQueryHandler
    implements QueryHandler<
        SearchSuiteConfigurationQuery,
        PaginatedProjection<ConfigurationSuiteWithWorkerProjection>> {

  private final SearchSuiteConfigurationPort searchSuiteConfigurationPort;
  private final WorkerRepositoryPort workerRepositoryPort;

  public SearchSuiteQueryHandler(
      SearchSuiteConfigurationPort searchSuiteConfigurationPort,
      WorkerRepositoryPort workerRepositoryPort) {
    this.searchSuiteConfigurationPort = searchSuiteConfigurationPort;
    this.workerRepositoryPort = workerRepositoryPort;
  }

  @Override
  public PaginatedProjection<ConfigurationSuiteWithWorkerProjection> execute(
      SearchSuiteConfigurationQuery query) {
    var paginatedSuites = searchSuiteConfigurationPort.search(query);
    var workers = workerRepositoryPort.findAll(query.environmentId());

    var suiteWithWorkers =
        paginatedSuites.getContent().stream()
            .map(suite -> ConfigurationSuiteWithWorkerProjection.from(suite, workers))
            .toList();

    return new PaginatedProjection<>(
        suiteWithWorkers,
        paginatedSuites.getCurrentPage(),
        paginatedSuites.getTotalPages(),
        paginatedSuites.getSize(),
        paginatedSuites.getTotalElements());
  }
}
