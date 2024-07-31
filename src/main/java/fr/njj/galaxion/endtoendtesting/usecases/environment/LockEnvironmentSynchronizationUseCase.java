package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.exception.EnvironmentAlreadyInSyncProgressException;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class LockEnvironmentSynchronizationUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;

    @Transactional
    public void execute(
            long environmentId) {

        var environment = environmentRetrievalService.get(environmentId);
        if (Boolean.TRUE.equals(environment.getIsLocked())) {
            throw new EnvironmentAlreadyInSyncProgressException();
        }
        environment.setIsLocked(true);
    }

}

