package fr.plum.e2e.manager.core.application.command.locker;

import fr.plum.e2e.manager.core.domain.port.LockManagerPort;
import fr.plum.e2e.manager.sharedkernel.application.command.NoParamCommandHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CleanLockManagerCommandHandler implements NoParamCommandHandler {

  private final LockManagerPort lockManagerPort;

  public CleanLockManagerCommandHandler(LockManagerPort lockManagerPort) {
    this.lockManagerPort = lockManagerPort;
  }

  @Override
  public void execute() {
    lockManagerPort.releaseAllLocks();
  }
}
