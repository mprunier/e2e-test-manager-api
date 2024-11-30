package fr.plum.e2e.manager.core.application;

import fr.plum.e2e.manager.core.domain.port.out.LockManagerPort;
import fr.plum.e2e.manager.core.domain.usecase.locker.CleanLockManagerUseCase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LockManagerFacade {
  private final CleanLockManagerUseCase lockManagerUseCase;

  public LockManagerFacade(LockManagerPort lockManagerPort) {
    this.lockManagerUseCase = new CleanLockManagerUseCase(lockManagerPort);
  }

  public void cleanAll() {
    lockManagerUseCase.execute();
  }
}
