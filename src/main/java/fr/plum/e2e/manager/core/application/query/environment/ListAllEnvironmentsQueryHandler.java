package fr.plum.e2e.manager.core.application.query.environment;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentProjection;
import fr.plum.e2e.manager.core.domain.port.view.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.sharedkernel.application.query.NoParamQueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ListAllEnvironmentsQueryHandler
    implements NoParamQueryHandler<List<EnvironmentProjection>> {

  private final ListAllEnvironmentsPort listAllEnvironmentsPort;

  public ListAllEnvironmentsQueryHandler(ListAllEnvironmentsPort listAllEnvironmentsPort) {
    this.listAllEnvironmentsPort = listAllEnvironmentsPort;
  }

  @Override
  public List<EnvironmentProjection> execute() {
    return listAllEnvironmentsPort.listAll();
  }
}
