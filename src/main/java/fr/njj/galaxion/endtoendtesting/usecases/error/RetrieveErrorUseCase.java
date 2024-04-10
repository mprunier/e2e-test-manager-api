package fr.njj.galaxion.endtoendtesting.usecases.error;

import fr.njj.galaxion.endtoendtesting.domain.response.SyncErrorResponse;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentSynchronizationErrorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveErrorUseCase {

    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;

    @Transactional
    public List<SyncErrorResponse> execute(
            long environmentId) {

        var entities = environmentSynchronizationErrorRepository.findByEnvironmentId(environmentId);

        var environmentErrors = new ArrayList<SyncErrorResponse>();
        entities.forEach(entity -> environmentErrors.add(
                SyncErrorResponse
                        .builder()
                        .file(entity.getFile())
                        .error(entity.getError())
                        .at(entity.getAt())
                        .build()));

        return environmentErrors;
    }

}

