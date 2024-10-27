package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.domain.response.SyncEnvironmentErrorResponse;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentSynchronizationErrorRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveEnvironmentErrorUseCase {

  private final EnvironmentSynchronizationErrorRetrievalService
      environmentSynchronizationErrorRetrievalService;

  @Transactional
  public List<SyncEnvironmentErrorResponse> execute(long environmentId) {

    var entities = environmentSynchronizationErrorRetrievalService.getByEnvironment(environmentId);

    var environmentErrors = new ArrayList<SyncEnvironmentErrorResponse>();
    entities.forEach(
        entity ->
            environmentErrors.add(
                SyncEnvironmentErrorResponse.builder()
                    .file(entity.getFile())
                    .error(entity.getError())
                    .at(entity.getAt())
                    .build()));

    return environmentErrors;
  }
}
