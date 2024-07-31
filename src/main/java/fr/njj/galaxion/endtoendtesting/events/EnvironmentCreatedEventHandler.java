package fr.njj.galaxion.endtoendtesting.events;

import fr.njj.galaxion.endtoendtesting.domain.event.EnvironmentCreatedEvent;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentCreatedEventHandler {

    private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void send(@Observes(during = TransactionPhase.AFTER_SUCCESS) EnvironmentCreatedEvent event) {
        globalEnvironmentSynchronizationUseCase.execute(event.getEnvironmentId());
    }
}

