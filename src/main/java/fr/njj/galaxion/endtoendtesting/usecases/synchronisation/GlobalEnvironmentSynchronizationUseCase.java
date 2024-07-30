package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncEnvironmentCompletedEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.CleanEnvironmentSynchronizationErrorService;
import fr.njj.galaxion.endtoendtesting.service.DeleteConfigurationTestService;
import fr.njj.galaxion.endtoendtesting.service.SynchronizeEnvironmentService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CloneGitlabRepositoryService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
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
import static fr.njj.galaxion.endtoendtesting.helper.FileHelper.cleanRepo;
import static fr.njj.galaxion.endtoendtesting.helper.GitHelper.getChangedFilesAfterDate;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GlobalEnvironmentSynchronizationUseCase {

    private final AddEnvironmentSynchronizationErrorUseCase addEnvironmentSynchronizationErrorUseCase;
    private final EnvironmentRetrievalService environmentRetrievalService;
    private final SynchronizeEnvironmentService synchronizeEnvironmentService;
    private final CloneGitlabRepositoryService cloneGitlabRepositoryService;
    private final DeleteConfigurationTestService deleteConfigurationTestService;
    private final Event<SyncEnvironmentCompletedEvent> syncEnvironmentEvent;
    private final CleanEnvironmentSynchronizationErrorService cleanEnvironmentSynchronizationErrorService;

    @Monitored(logExit = false)
    @Transactional
    public void execute(
            long environmentId) {

        var environment = environmentRetrievalService.getEnvironment(environmentId);
        cleanEnvironmentSynchronizationErrorService.cleanErrors(environment.getId(), null);

        var errors = new HashMap<String, String>();
        var projectFolder = cloneGitlabRepositoryService.cloneRepo(environment.getProjectId(), environment.getId().toString(), environment.getToken(), environment.getBranch());

        try {
            var commitAfterDate = ZonedDateTime.of(LocalDateTime.now().minusYears(10), ZoneId.systemDefault());
            var changedFiles = getChangedFilesAfterDate(projectFolder, commitAfterDate);
            cleanFilesRemoved(projectFolder, environment);
            synchronizeEnvironmentService.synchronize(environment, changedFiles, projectFolder, errors);
        } catch (Exception exception) {
            errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
            log.error("Error during synchronization for Environment id [{}] : {}.", environment.getId(), exception.getMessage());
        }

        cleanRepo(environment, projectFolder, errors);
        errors.forEach((file, error) -> addEnvironmentSynchronizationErrorUseCase.execute(environment.getId(), file, error));
        syncEnvironmentEvent.fire(SyncEnvironmentCompletedEvent.builder().environmentId(environmentId).build());
    }

    private void cleanFilesRemoved(File projectFolder, EnvironmentEntity environment) {
        var allTestFiles = listAllTestFiles(projectFolder);
        var allConfTestFiles = environment.getConfigurationSuites().stream().map(ConfigurationSuiteEntity::getFile).toList();
        allConfTestFiles.forEach(file -> {
            if (!allTestFiles.contains(file)) {
                deleteConfigurationTestService.deleteByFile(file, environment.getId());
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

