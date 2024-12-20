package fr.plum.e2e.manager.core.domain.usecase.locker;

import fr.plum.e2e.manager.core.domain.port.out.LockManagerPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.NoParamCommandUseCase;

public class CleanLockManagerUseCase implements NoParamCommandUseCase {

  private final LockManagerPort lockManagerPort;

  public CleanLockManagerUseCase(LockManagerPort lockManagerPort) {
    this.lockManagerPort = lockManagerPort;
  }

  @Override
  public void execute() {
    lockManagerPort.releaseAllLocks();
  }
}
