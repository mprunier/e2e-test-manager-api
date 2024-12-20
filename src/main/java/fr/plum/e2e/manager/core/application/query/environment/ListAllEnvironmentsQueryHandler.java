package fr.plum.e2e.manager.core.application.query.environment;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentProjection;
import fr.plum.e2e.manager.core.domain.port.out.view.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamQueryHandler;
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
