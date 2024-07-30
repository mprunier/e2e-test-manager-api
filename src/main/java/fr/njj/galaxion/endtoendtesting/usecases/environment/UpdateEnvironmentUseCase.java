package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.exception.HiddenVariableException;
import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentVariableRepository;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class UpdateEnvironmentUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final SecurityIdentity identity;
    private final EnvironmentVariableRepository environmentVariableRepository;

    @Transactional
    public void execute(
            Long environmentId,
            CreateUpdateEnvironmentRequest request) {
        var environment = environmentRetrievalService.getEnvironment(environmentId);
        request.getVariables().forEach(variableRequest -> {
            var variableOptional = environmentVariableRepository.findByName(environmentId, variableRequest.getName());
            if (variableOptional.isPresent() && (Boolean.TRUE.equals(variableOptional.get().getIsHidden()) && variableRequest.getDefaultValue().contains("**********"))) {
                if (Boolean.FALSE.equals(variableRequest.getIsHidden())) {
                    throw new HiddenVariableException();
                }
                variableRequest.setDefaultValue(variableOptional.get().getDefaultValue());

            }
        });

        environment.getVariables().clear();
        environment.setDescription(request.getDescription());
        environment.setBranch(request.getBranch());
        if (!request.getToken().contains("****")) {
            environment.setToken(request.getToken());
        }
        environment.setProjectId(request.getProjectId());
        environment.setUpdatedBy(identity.getPrincipal().getName());
        environment.setUpdatedAt(ZonedDateTime.now());
        createVariables(request, environment);
    }

    private void createVariables(CreateUpdateEnvironmentRequest request, EnvironmentEntity environment) {
        request.getVariables()
               .forEach(variable -> EnvironmentVariableEntity.builder()
                                                             .environment(environment)
                                                             .name(variable.getName())
                                                             .defaultValue(variable.getDefaultValue())
                                                             .description(variable.getDescription())
                                                             .isHidden(variable.getIsHidden() != null ? variable.getIsHidden() : false)
                                                             .build()
                                                             .persist());
    }
}
