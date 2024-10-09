package fr.njj.galaxion.endtoendtesting.usecases.synchronisation;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.GLOBAL_ENVIRONMENT_ERROR;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.FileHelper.cleanRepo;

import fr.njj.galaxion.endtoendtesting.domain.event.SyncEnvironmentCompletedEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.CleanEnvironmentSynchronizationErrorService;
import fr.njj.galaxion.endtoendtesting.service.CreateOrUpdateEnvironmentSynchronizationErrorService;
import fr.njj.galaxion.endtoendtesting.service.DeleteConfigurationTestAndSuiteService;
import fr.njj.galaxion.endtoendtesting.service.DeleteFileGroupService;
import fr.njj.galaxion.endtoendtesting.service.SynchronizeEnvironmentService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CloneGitlabRepositoryService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PartialEnvironmentSynchronizationUseCase {

  private final CreateOrUpdateEnvironmentSynchronizationErrorService
      createOrUpdateEnvironmentSynchronizationErrorService;
  private final EnvironmentRetrievalService environmentRetrievalService;
  private final SynchronizeEnvironmentService synchronizeEnvironmentService;
  private final CloneGitlabRepositoryService cloneGitlabRepositoryService;
  private final DeleteConfigurationTestAndSuiteService deleteConfigurationTestAndSuiteService;
  private final CleanEnvironmentSynchronizationErrorService
      cleanEnvironmentSynchronizationErrorService;
  private final DeleteFileGroupService deleteFileGroupService;

  private final Event<SyncEnvironmentCompletedEvent> syncEnvironmentEvent;

  @Monitored(logExit = false)
  @Transactional
  public void execute(
      String projectId, String branch, Set<String> filesToSynchronize, Set<String> filesToRemove) {

    var environments =
        environmentRetrievalService.getEnvironmentsByBranchAndProjectId(branch, projectId);
    for (EnvironmentEntity environment : environments) {
      cleanEnvironmentErrors(filesToSynchronize, filesToRemove, environment);
      cleanFilesToRemove(filesToRemove, environment);
      updateFilesToSynchronize(filesToSynchronize, environment);
      syncEnvironmentEvent.fire(
          SyncEnvironmentCompletedEvent.builder().environmentId(environment.getId()).build());
    }
  }

  private void updateFilesToSynchronize(
      Set<String> filesToSynchronize, EnvironmentEntity environment) {
    var errors = new HashMap<String, String>();
    var projectFolder =
        cloneGitlabRepositoryService.cloneRepo(
            environment.getProjectId(),
            environment.getId().toString(),
            environment.getToken(),
            environment.getBranch());

    try {
      synchronizeEnvironmentService.synchronize(
          environment, filesToSynchronize, projectFolder, errors);
    } catch (Exception exception) {
      errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
      log.error(
          "Error during synchronization for Environment id [{}] : {}.",
          environment.getId(),
          exception.getMessage());
    }

    cleanRepo(environment, projectFolder, errors);
    errors.forEach(
        (file, error) ->
            createOrUpdateEnvironmentSynchronizationErrorService.createOrUpdateSynchronizationError(
                environment.getId(), file, error));
  }

  private void cleanEnvironmentErrors(
      Set<String> filesToSynchronize, Set<String> filesToRemove, EnvironmentEntity environment) {
    filesToSynchronize.forEach(
        file -> cleanEnvironmentSynchronizationErrorService.cleanErrors(environment.getId(), file));
    filesToRemove.forEach(
        file -> cleanEnvironmentSynchronizationErrorService.cleanErrors(environment.getId(), file));
    cleanEnvironmentSynchronizationErrorService.cleanErrors(
        environment.getId(), GLOBAL_ENVIRONMENT_ERROR);
  }

  private void cleanFilesToRemove(Set<String> filesToRemove, EnvironmentEntity environment) {
    filesToRemove.forEach(
        relativePathString -> {
          var file = relativePathString.split(START_PATH)[1];
          deleteConfigurationTestAndSuiteService.deleteByEnvAndFile(environment.getId(), file);
          deleteFileGroupService.deleteByEnvAndFile(environment.getId(), file);
        });
  }
}
