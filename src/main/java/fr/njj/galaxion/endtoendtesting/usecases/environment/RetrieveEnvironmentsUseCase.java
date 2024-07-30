package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveEnvironmentsUseCase {

    private final EnvironmentRepository environmentRepository;

    @Transactional
    public List<EnvironmentResponse> execute() {
        var environments = environmentRepository.findAllEnvironmentsEnabled();
        return environments
                .stream()
                .map(this::buildEnvironmentResponse)
                .toList();
    }

    private EnvironmentResponse buildEnvironmentResponse(EnvironmentEntity entity) {
        return EnvironmentResponse
                .builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .build();
    }
}
