package fr.njj.galaxion.endtoendtesting.usecases.environment;

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
public class ToggleEnvironmentUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final SecurityIdentity identity;

    @Transactional
    public void execute(
            Long environmentId,
            Boolean isEnabled) {
        var environment = environmentRetrievalService.getEnvironment(environmentId);
        environment.setIsEnabled(isEnabled);
        environment.setUpdatedBy(identity.getPrincipal().getName());
        environment.setUpdatedAt(ZonedDateTime.now());
    }
}
