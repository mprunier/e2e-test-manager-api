package fr.plum.e2e.manager.core.infrastructure.primary.system;

import fr.plum.e2e.manager.core.application.SynchronizationFacade;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CleanSynchronizationProcess {

  private final SynchronizationFacade synchronizationFacade;

  void onStart(@Observes StartupEvent e) {
    synchronizationFacade.cleanAllSynchronizations();
  }
}
