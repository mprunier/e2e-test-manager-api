package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentSynchronizationErrorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CleanEnvironmentSynchronizationErrorUseCase {

    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;

    @Transactional
    public void execute(
            long environmentId,
            String file) {

        if (file != null) {
            environmentSynchronizationErrorRepository.deleteByEnvironmentIdAndFile(environmentId, file);
        } else {
            environmentSynchronizationErrorRepository.deleteByEnvironmentId(environmentId);
        }
    }
}

