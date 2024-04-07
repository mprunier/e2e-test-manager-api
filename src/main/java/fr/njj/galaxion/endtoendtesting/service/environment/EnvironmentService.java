package fr.njj.galaxion.endtoendtesting.service.environment;

import fr.njj.galaxion.endtoendtesting.domain.exception.HiddenVariableException;
import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentVariableRepository;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSchedulerService;
import fr.njj.galaxion.endtoendtesting.usecases.environment.LockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.environment.UnLockEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final SecurityIdentity identity;
    private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;
    private final ConfigurationSchedulerService configurationSchedulerService;
    private final EnvironmentVariableRepository environmentVariableRepository;
    private final LockEnvironmentSynchronizationUseCase lockEnvironmentSynchronizationUseCase;
    private final UnLockEnvironmentSynchronizationUseCase unLockEnvironmentSynchronizationUseCase;

    @Transactional
    public EnvironmentResponse create(CreateUpdateEnvironmentRequest request) {
        environmentRetrievalService.assertBranchNotExist(request.getBranch(), request.getProjectId());
        var username = identity.getPrincipal().getName();
        var environment = EnvironmentEntity.builder()
                                           .description(request.getDescription())
                                           .branch(request.getBranch())
                                           .token(request.getToken())
                                           .projectId(request.getProjectId())
                                           .isLocked(true)
                                           .createdBy(username)
                                           .build();
        environment.persist();
        createVariables(request, environment);
        configurationSchedulerService.create(environment);
        synchronize(environment);
        return environmentRetrievalService.getEnvironmentResponse(environment.getId());
    }

    private void synchronize(EnvironmentEntity environment) {
        CompletableFuture.runAsync(() -> {
            try {
                globalEnvironmentSynchronizationUseCase.execute(environment.getId());
            } finally {
                unLockEnvironmentSynchronizationUseCase.execute(environment.getId());
            }
        });
    }

    private static void createVariables(CreateUpdateEnvironmentRequest request, EnvironmentEntity environment) {
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

    @Transactional
    public void update(Long id, CreateUpdateEnvironmentRequest request) {
        var environment = environmentRetrievalService.getEnvironment(id);
        if (!environment.getBranch().equals(request.getBranch()) || !environment.getProjectId().equals(request.getProjectId())) {
            environmentRetrievalService.assertBranchNotExist(request.getBranch(), request.getProjectId());
        }

        request.getVariables().forEach(variableRequest -> {
            var variableOptional = environmentVariableRepository.findByName(id, variableRequest.getName());
            if (variableOptional.isPresent()) {
                if (Boolean.TRUE.equals(variableOptional.get().getIsHidden()) && variableRequest.getDefaultValue().contains("**********")) {
                    if (Boolean.FALSE.equals(variableRequest.getIsHidden())) {
                        throw new HiddenVariableException();
                    }
                    variableRequest.setDefaultValue(variableOptional.get().getDefaultValue());
                }
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

    @Transactional
    public void updateIsEnabled(Long id, Boolean isEnabled) {
        var environment = environmentRetrievalService.getEnvironment(id);
        environment.setIsEnabled(isEnabled);
        environment.setUpdatedBy(identity.getPrincipal().getName());
        environment.setUpdatedAt(ZonedDateTime.now());
    }
}
