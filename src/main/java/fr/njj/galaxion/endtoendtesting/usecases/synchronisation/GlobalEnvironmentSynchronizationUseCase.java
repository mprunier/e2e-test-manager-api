package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncErrorsEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationService;
import fr.njj.galaxion.endtoendtesting.service.configuration.EnvironmentSynchronizationService;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.usecases.cache.CleanCacheAfterSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.error.RetrieveErrorUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.GLOBAL_ENVIRONMENT_ERROR;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.GitHelper.getChangedFilesAfterDate;
import static fr.njj.galaxion.endtoendtesting.websocket.EventsWebSocket.sendEventToEnvironmentSessions;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GlobalEnvironmentSynchronizationUseCase {

    private final AddEnvironmentSynchronizationErrorUseCase addEnvironmentSynchronizationErrorUseCase;
    private final EnvironmentRetrievalService environmentRetrievalService;
    private final EnvironmentSynchronizationService environmentSynchronizationService;
    private final GitlabService gitlabService;
    private final ConfigurationService configurationService;
    private final CleanCacheAfterSynchronizationUseCase cleanCacheAfterSynchronizationUseCase;
    private final RetrieveErrorUseCase retrieveErrorUseCase;

    @Monitored
    @Transactional
    public void execute(
            long environmentId) {

        var environment = environmentRetrievalService.getEnvironment(environmentId);
        environmentSynchronizationService.cleanErrors(environment.getId(), null);

        var errors = new HashMap<String, String>();
        var projectFolder = gitlabService.cloneRepo(environment.getProjectId(), environment.getId().toString(), environment.getToken(), environment.getBranch());

        try {
            var commitAfterDate = ZonedDateTime.of(LocalDateTime.now().minusYears(10), ZoneId.systemDefault());
            var changedFiles = getChangedFilesAfterDate(projectFolder, commitAfterDate);
            cleanFilesRemoved(projectFolder, environment);
            environmentSynchronizationService.synchronize(environment, changedFiles, projectFolder, errors);
        } catch (Exception exception) {
            errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
            log.error("Error during synchronization for Environment id [{}] : {}.", environment.getId(), exception.getMessage());
        }

        EnvironmentSynchronizationService.cleanRepo(environment, projectFolder, errors);
        errors.forEach((file, error) -> addEnvironmentSynchronizationErrorUseCase.execute(environment.getId(), file, error));
        cleanCacheAfterSynchronizationUseCase.execute(environmentId);

        sendErrorsEvent(environment);
    }

    private void sendErrorsEvent(EnvironmentEntity environment) {
        var allErrors = retrieveErrorUseCase.execute(environment.getId());
        sendEventToEnvironmentSessions(environment.getId().toString(), SyncErrorsEvent.builder().syncErrors(allErrors).build());
    }

    private void cleanFilesRemoved(File projectFolder, EnvironmentEntity environment) {
        var allTestFiles = listAllTestFiles(projectFolder);
        var allConfTestFiles = environment.getConfigurationSuites().stream().map(ConfigurationSuiteEntity::getFile).toList();
        allConfTestFiles.forEach(file -> {
            if (!allTestFiles.contains(file)) {
                configurationService.deleteConfigurationByFile(file, environment.getId());
            }
        });
    }

    private Set<String> listAllTestFiles(File parentDirectory) {
        var allTestFilePaths = new HashSet<String>();
        var subFolderPath = Paths.get(parentDirectory.getAbsolutePath(), START_PATH);
        if (Files.exists(subFolderPath) && Files.isDirectory(subFolderPath)) {
            try (var paths = Files.walk(subFolderPath)) {
                var fileList = paths.filter(Files::isRegularFile).toList();
                for (var path : fileList) {
                    int index = path.toString().indexOf(START_PATH);
                    if (index != -1) {
                        allTestFilePaths.add(path.toString().substring(index + START_PATH.length()));
                    }
                }
            } catch (IOException exception) {
                throw new ConfigurationSynchronizationException("List Files Error : " + exception.getMessage());
            }
        }
        return allTestFilePaths;
    }
}

