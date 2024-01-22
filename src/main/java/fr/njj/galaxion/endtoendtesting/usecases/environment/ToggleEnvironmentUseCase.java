package fr.njj.galaxion.endtoendtesting.usecases.environment;

import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ToggleEnvironmentUseCase {

  private final EnvironmentRetrievalService environmentRetrievalService;

  private final SecurityIdentity identity;

  @Transactional
  public void execute(Long environmentId, Boolean isEnabled) {
    var environment = environmentRetrievalService.get(environmentId);
    environment.setIsEnabled(isEnabled);
    environment.setUpdatedBy(identity.getPrincipal().getName());
    environment.setUpdatedAt(ZonedDateTime.now());
  }
}
