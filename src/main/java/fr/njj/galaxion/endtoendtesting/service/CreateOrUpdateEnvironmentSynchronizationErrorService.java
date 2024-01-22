package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentSynchronizationErrorEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentSynchronizationErrorRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CreateOrUpdateEnvironmentSynchronizationErrorService {

  private final EnvironmentSynchronizationErrorRetrievalService
      environmentSynchronizationErrorRetrievalService;
  private final EnvironmentRetrievalService environmentRetrievalService;

  @Transactional
  public void createOrUpdateSynchronizationError(long environmentId, String file, String error) {

    var environment = environmentRetrievalService.get(environmentId);

    var optionalEntity =
        environmentSynchronizationErrorRetrievalService.getByEnvAndFile(environment.getId(), file);
    if (optionalEntity.isPresent()) {
      update(error, optionalEntity.get());
    } else {
      create(environment, file, error);
    }
  }

  private static void create(EnvironmentEntity environment, String file, String error) {
    EnvironmentSynchronizationErrorEntity.builder()
        .environment(environment)
        .file(file)
        .error(error)
        .build()
        .persist();
  }

  private static void update(String error, EnvironmentSynchronizationErrorEntity entity) {
    entity.setError(error);
    entity.setAt(ZonedDateTime.now());
  }
}
