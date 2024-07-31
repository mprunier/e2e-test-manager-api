package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentSynchronizationErrorEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentSynchronizationErrorRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentSynchronizationErrorRetrievalService {

    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;

    @Transactional
    public Optional<EnvironmentSynchronizationErrorEntity> getByEnvAndFile(
            long environmentId,
            String file) {
        return environmentSynchronizationErrorRepository.findByEnvironmentIdAndFile(environmentId, file);
    }

    @Transactional
    public List<EnvironmentSynchronizationErrorEntity> getByEnvironment(
            long environmentId) {
        return environmentSynchronizationErrorRepository.findByEnvironmentId(environmentId);
    }
}
