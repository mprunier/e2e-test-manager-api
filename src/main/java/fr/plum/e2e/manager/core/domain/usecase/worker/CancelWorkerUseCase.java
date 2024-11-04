package fr.plum.e2e.manager.core.domain.usecase.worker;

import fr.plum.e2e.manager.core.domain.model.command.CancelWorkerCommand;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.CommandUseCase;

public class CancelWorkerUseCase implements CommandUseCase<CancelWorkerCommand> {

  @Override
  public void execute(CancelWorkerCommand command) {}
}
