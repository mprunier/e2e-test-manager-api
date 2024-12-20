package fr.plum.e2e.manager.core.domain.usecase.environment;

import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.view.EnvironmentDetailsView;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetEnvironmentDetailsUseCase
    implements QueryUseCase<CommonQuery, EnvironmentDetailsView> {

  private final EnvironmentRepositoryPort environmentRepositoryPort;

  public GetEnvironmentDetailsUseCase(EnvironmentRepositoryPort environmentRepositoryPort) {
    this.environmentRepositoryPort = environmentRepositoryPort;
  }

  @Override
  public EnvironmentDetailsView execute(CommonQuery query) {
    return environmentRepositoryPort.findDetails(query.environmentId());
  }
}
