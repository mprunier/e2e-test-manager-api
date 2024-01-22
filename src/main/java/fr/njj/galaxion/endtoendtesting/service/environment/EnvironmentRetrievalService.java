package fr.njj.galaxion.endtoendtesting.service.environment;

import fr.njj.galaxion.endtoendtesting.domain.exception.EnvironmentBranchAlreadyExistException;
import fr.njj.galaxion.endtoendtesting.domain.exception.EnvironmentNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.mapper.EnvironmentResponseMapper.buildEnvironmentResponse;
import static fr.njj.galaxion.endtoendtesting.mapper.EnvironmentResponseMapper.buildEnvironmentResponses;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentRetrievalService {

    private final EnvironmentRepository environmentRepository;

    public EnvironmentResponse getEnvironmentResponse(long id) {
        return buildEnvironmentResponse(getEnvironment(id), true);
    }

    public List<EnvironmentResponse> getEnvironments() {
        return buildEnvironmentResponses(environmentRepository.findAllEnvironmentsEnabled(), false);
    }

    public EnvironmentEntity getEnvironment(long id) {
        return environmentRepository.findByIdOptional(id)
                                    .orElseThrow(() -> new EnvironmentNotFoundException(id));
    }

    public void assertBranchNotExist(String branch, String projectId) {
        var environment = environmentRepository.findByBranchAndProjectId(branch, projectId);
        if (environment.isPresent()) {
            throw new EnvironmentBranchAlreadyExistException(branch);
        }
    }
}
