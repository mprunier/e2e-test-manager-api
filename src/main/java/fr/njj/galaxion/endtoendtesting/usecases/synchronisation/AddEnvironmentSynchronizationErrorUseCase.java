package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentSynchronizationErrorEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentSynchronizationErrorRepository;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AddEnvironmentSynchronizationErrorUseCase {

    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;
    private final EnvironmentRetrievalService environmentRetrievalService;

    @Transactional
    public void execute(
            long environmentId,
            String file,
            String error) {

        var environment = environmentRetrievalService.getEnvironment(environmentId);

        var optionalEntity = environmentSynchronizationErrorRepository.findByEnvironmentIdAndFile(environment.getId(), file);
        if (optionalEntity.isPresent()) {
            update(error, optionalEntity.get());
        } else {
            create(environment, file, error);
        }
    }

    private static void create(EnvironmentEntity environment, String file, String error) {
        EnvironmentSynchronizationErrorEntity
                .builder()
                .environment(environment)
                .file(file)
                .error(error)
                .build()
                .persist();
    }

    private static void update(String error, EnvironmentSynchronizationErrorEntity entity) {
        entity.setError(error);
        entity.setAt(ZonedDateTime.now());
    }

}

