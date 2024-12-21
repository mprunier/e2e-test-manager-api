package fr.plum.e2e.manager.core.application.query.environment;

import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsProjection;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetEnvironmentDetailsQueryHandler
    implements QueryHandler<CommonQuery, EnvironmentDetailsProjection> {

  private final EnvironmentRepositoryPort environmentRepositoryPort;

  public GetEnvironmentDetailsQueryHandler(EnvironmentRepositoryPort environmentRepositoryPort) {
    this.environmentRepositoryPort = environmentRepositoryPort;
  }

  @Override
  public EnvironmentDetailsProjection execute(CommonQuery query) {
    var environmentDetails = environmentRepositoryPort.findDetails(query.environmentId());
    if (environmentDetails == null) {
      throw new EnvironmentNotFoundException(query.environmentId());
    }
    return environmentDetails;
  }
}
