package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSchedulerEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.GlobalEnvironmentSynchronizationUseCase;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CreateEnvironmentUseCase {

    private final RetrieveEnvironmentDetailsUseCase retrieveEnvironmentDetailsUseCase;
    private final GlobalEnvironmentSynchronizationUseCase globalEnvironmentSynchronizationUseCase;

    private final SecurityIdentity identity;

    @Transactional
    public EnvironmentResponse execute(
            CreateUpdateEnvironmentRequest request) {
        var username = identity.getPrincipal().getName();
        var environment = EnvironmentEntity
                .builder()
                .description(request.getDescription())
                .branch(request.getBranch())
                .token(request.getToken())
                .projectId(request.getProjectId())
                .isLocked(true)
                .createdBy(username)
                .build();
        environment.persist();
        createVariables(request, environment);
        createConfigurationScheduler(environment);
        synchronize(environment);
        return retrieveEnvironmentDetailsUseCase.execute(environment.getId());
    }

    private void createConfigurationScheduler(EnvironmentEntity environment) {
        ConfigurationSchedulerEntity
                .builder()
                .environment(environment)
                .build()
                .persist();
    }

    private void synchronize(EnvironmentEntity environment) {
        CompletableFuture.runAsync(() -> globalEnvironmentSynchronizationUseCase.execute(environment.getId()));
    }

    private void createVariables(CreateUpdateEnvironmentRequest request, EnvironmentEntity environment) {
        request.getVariables()
               .forEach(variable -> EnvironmentVariableEntity
                       .builder()
                       .environment(environment)
                       .name(variable.getName())
                       .defaultValue(variable.getDefaultValue())
                       .description(variable.getDescription())
                       .isHidden(variable.getIsHidden() != null && variable.getIsHidden())
                       .build()
                       .persist());
    }
}
