package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncEnvironmentCompletedEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationService;
import fr.njj.galaxion.endtoendtesting.service.configuration.EnvironmentSynchronizationService;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Set;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.GLOBAL_ENVIRONMENT_ERROR;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PartialEnvironmentSynchronizationUseCase {

    private final AddEnvironmentSynchronizationErrorUseCase addEnvironmentSynchronizationErrorUseCase;
    private final EnvironmentRetrievalService environmentRetrievalService;
    private final EnvironmentSynchronizationService environmentSynchronizationService;
    private final GitlabService gitlabService;
    private final ConfigurationService configurationService;

    private final Event<SyncEnvironmentCompletedEvent> syncEnvironmentEvent;

    @Monitored
    @Transactional
    public void execute(
            String projectId,
            String branch,
            Set<String> filesToSynchronize,
            Set<String> filesToRemove) {

        var environments = environmentRetrievalService.getEnvironmentsByBranchAndProjectId(branch, projectId);
        for (EnvironmentEntity environment : environments) {
            cleanEnvironmentErrors(filesToSynchronize, filesToRemove, environment);
            cleanFilesToRemove(filesToRemove, environment);
            updateFilesToSynchronize(filesToSynchronize, environment);
            syncEnvironmentEvent.fire(SyncEnvironmentCompletedEvent.builder().environmentId(environment.getId()).build());
        }
    }

    private void updateFilesToSynchronize(Set<String> filesToSynchronize, EnvironmentEntity environment) {
        var errors = new HashMap<String, String>();
        var projectFolder = gitlabService.cloneRepo(environment.getProjectId(), environment.getId().toString(), environment.getToken(), environment.getBranch());

        try {
            environmentSynchronizationService.synchronize(environment, filesToSynchronize, projectFolder, errors);
        } catch (Exception exception) {
            errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
            log.error("Error during synchronization for Environment id [{}] : {}.", environment.getId(), exception.getMessage());
        }

        EnvironmentSynchronizationService.cleanRepo(environment, projectFolder, errors);
        errors.forEach((file, error) -> addEnvironmentSynchronizationErrorUseCase.execute(environment.getId(), file, error));
    }

    private void cleanEnvironmentErrors(Set<String> filesToSynchronize, Set<String> filesToRemove, EnvironmentEntity environment) {
        filesToSynchronize.forEach(file -> environmentSynchronizationService.cleanErrors(environment.getId(), file));
        filesToRemove.forEach(file -> environmentSynchronizationService.cleanErrors(environment.getId(), file));
        environmentSynchronizationService.cleanErrors(environment.getId(), GLOBAL_ENVIRONMENT_ERROR);
    }

    private void cleanFilesToRemove(Set<String> filesToRemove, EnvironmentEntity environment) {
        filesToRemove.forEach(file -> configurationService.deleteConfigurationByFile(file, environment.getId()));
    }
}

