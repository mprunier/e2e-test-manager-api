package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UnLockEnvironmentSynchronizationUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;

    @Transactional
    public void execute(
            long environmentId) {

        var environment = environmentRetrievalService.get(environmentId);
        environment.setIsLocked(false);
    }

}

