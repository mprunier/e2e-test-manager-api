package fr.plum.e2e.manager.core.domain.usecase.environment;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentView;
import fr.plum.e2e.manager.core.domain.port.out.query.ListAllEnvironmentsPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamQueryUseCase;
import java.util.List;

public class ListAllEnvironmentsUseCase implements NoParamQueryUseCase<List<EnvironmentView>> {

  private final ListAllEnvironmentsPort listAllEnvironmentsPort;

  public ListAllEnvironmentsUseCase(ListAllEnvironmentsPort listAllEnvironmentsPort) {
    this.listAllEnvironmentsPort = listAllEnvironmentsPort;
  }

  @Override
  public List<EnvironmentView> execute() {
    return listAllEnvironmentsPort.listAll();
  }
}
