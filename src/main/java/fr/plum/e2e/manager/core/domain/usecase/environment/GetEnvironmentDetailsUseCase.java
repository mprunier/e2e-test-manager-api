package fr.plum.e2e.manager.core.domain.usecase.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.EnvironmentService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetEnvironmentDetailsUseCase implements QueryUseCase<CommonQuery, Environment> {

  private final EnvironmentService environmentService;

  public GetEnvironmentDetailsUseCase(EnvironmentRepositoryPort environmentRepositoryPort) {
    this.environmentService = new EnvironmentService(environmentRepositoryPort);
  }

  @Override
  public Environment execute(CommonQuery query) {
    return environmentService.getEnvironment(query.environmentId());
  }
}
