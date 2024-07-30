package fr.njj.galaxion.endtoendtesting.service.environment;

import fr.njj.galaxion.endtoendtesting.domain.exception.EnvironmentNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.mapper.EnvironmentResponseMapper.buildEnvironmentResponse;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentRetrievalService {

    private final EnvironmentRepository environmentRepository;

    @Transactional
    public EnvironmentResponse getEnvironmentResponse(long id) {
        return buildEnvironmentResponse(getEnvironment(id), true);
    }


    @Transactional
    public EnvironmentEntity getEnvironment(long id) {
        return environmentRepository.findByIdOptional(id)
                                    .orElseThrow(() -> new EnvironmentNotFoundException(id));
    }

    @Transactional
    public List<EnvironmentEntity> getEnvironments() {
        return environmentRepository.findAll().stream().toList();
    }

    @Transactional
    public List<EnvironmentEntity> getEnvironmentsByBranchAndProjectId(String branch, String projectId) {
        return environmentRepository.findAllByBranchAndProjectId(branch, projectId);
    }
}
