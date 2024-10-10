package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.event.EnvironmentCreatedEvent;
import fr.njj.galaxion.endtoendtesting.domain.request.CreateUpdateEnvironmentRequest;
import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSchedulerEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentVariableEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CreateEnvironmentUseCase {

  private final EnvironmentRetrievalService environmentRetrievalService;

  private final Event<EnvironmentCreatedEvent> environmentCreatedEvent;

  private final SecurityIdentity identity;

  @Transactional
  public EnvironmentResponse execute(CreateUpdateEnvironmentRequest request) {
    var username = identity.getPrincipal().getName();
    var environment =
        EnvironmentEntity.builder()
            .description(request.getDescription())
            .branch(request.getBranch())
            .token(request.getToken())
            .projectId(request.getProjectId())
            .maxParallelFileSize(request.getMaxParallelTestNumber())
            .isLocked(true)
            .createdBy(username)
            .build();
    environment.persist();
    createVariables(request, environment);
    createConfigurationScheduler(environment);
    environmentCreatedEvent.fire(
        EnvironmentCreatedEvent.builder().environmentId(environment.getId()).build());
    return environmentRetrievalService.getResponse(environment.getId());
  }

  private void createConfigurationScheduler(EnvironmentEntity environment) {
    ConfigurationSchedulerEntity.builder().environment(environment).build().persist();
  }

  private void createVariables(
      CreateUpdateEnvironmentRequest request, EnvironmentEntity environment) {
    request
        .getVariables()
        .forEach(
            variable ->
                EnvironmentVariableEntity.builder()
                    .environment(environment)
                    .name(variable.getName())
                    .defaultValue(variable.getDefaultValue())
                    .description(variable.getDescription())
                    .isHidden(variable.getIsHidden() != null && variable.getIsHidden())
                    .build()
                    .persist());
  }
}
