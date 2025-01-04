package fr.plum.e2e.manager.core.application.query.environment;

import fr.plum.e2e.manager.core.domain.model.exception.EnvironmentNotFoundException;
import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentDetailsProjection;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.view.GetEnvironmentDetailsPort;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetEnvironmentDetailsQueryHandler
    implements QueryHandler<CommonQuery, EnvironmentDetailsProjection> {

  private final GetEnvironmentDetailsPort getEnvironmentDetailsPort;

  public GetEnvironmentDetailsQueryHandler(GetEnvironmentDetailsPort getEnvironmentDetailsPort) {
    this.getEnvironmentDetailsPort = getEnvironmentDetailsPort;
  }

  @Override
  public EnvironmentDetailsProjection execute(CommonQuery query) {
    var environmentDetails = getEnvironmentDetailsPort.find(query.environmentId());
    if (environmentDetails == null) {
      throw new EnvironmentNotFoundException(query.environmentId());
    }
    return environmentDetails;
  }
}
