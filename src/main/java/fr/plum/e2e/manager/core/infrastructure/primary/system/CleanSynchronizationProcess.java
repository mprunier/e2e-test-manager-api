package fr.plum.e2e.manager.core.infrastructure.primary.system;

import fr.plum.e2e.manager.core.application.command.synchronization.CleanAllSynchronizationCommandHandler;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class CleanSynchronizationProcess {

  private final CleanAllSynchronizationCommandHandler cleanAllSynchronizationCommandHandler;

  void onStart(@Observes StartupEvent e) {
    cleanAllSynchronizationCommandHandler.execute();
  }
}
