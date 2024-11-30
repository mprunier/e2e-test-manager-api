package fr.plum.e2e.manager.core.infrastructure.primary.system;

import fr.plum.e2e.manager.core.application.LockManagerFacade;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CleanLockManagerProcess {

  private final LockManagerFacade lockManagerFacade;

  void onStart(@Observes StartupEvent e) {
    lockManagerFacade.cleanAll();
  }
}
