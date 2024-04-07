package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentErrorResponse;
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
public class RetrieveEnvironmentErrorUseCase {

    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;

    @Transactional
    public List<EnvironmentErrorResponse> execute(
            long environmentId) {

        var entities = environmentSynchronizationErrorRepository.findByEnvironmentId(environmentId);
        
        var environmentErrors = new ArrayList<EnvironmentErrorResponse>();
        entities.forEach(entity -> EnvironmentErrorResponse
                .builder()
                .error(entity.getError())
                .at(entity.getAt())
                .build());

        return environmentErrors;
    }

}

