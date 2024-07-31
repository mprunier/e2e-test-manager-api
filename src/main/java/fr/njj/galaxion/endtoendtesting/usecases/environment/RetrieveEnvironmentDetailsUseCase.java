package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.mapper.EnvironmentVariableResponseMapper.buildEnvironmentVariableResponses;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveEnvironmentDetailsUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;

    @Transactional
    public EnvironmentResponse execute(long id) {
        var environment = environmentRetrievalService.get(id);
        return buildEnvironmentResponse(environment);
    }

    private EnvironmentResponse buildEnvironmentResponse(EnvironmentEntity entity) {
        var variables = buildEnvironmentVariableResponses(entity.getVariables());
        return EnvironmentResponse
                .builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .projectId(entity.getProjectId())
                .token(hideToken(entity.getToken()))
                .branch(entity.getBranch())
                .isEnabled(entity.getIsEnabled())
                .isLocked(entity.getIsLocked())
                .isRunningAllTests(entity.getIsRunningAllTests())
                .lastAllTestsError(entity.getLastALlTestsError())
                .variables(variables)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private String hideToken(String token) {
        if (token == null || token.length() <= 6) {
            return "**********";
        }

        StringBuilder tokenTransforme = new StringBuilder(token);
        for (int i = 3; i < token.length() - 3; i++) {
            tokenTransforme.setCharAt(i, '*');
        }

        return tokenTransforme.toString();
    }
}
