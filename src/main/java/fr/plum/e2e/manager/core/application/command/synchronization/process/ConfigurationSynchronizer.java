package fr.plum.e2e.manager.core.application.command.synchronization.process;

import fr.plum.e2e.manager.core.application.command.synchronization.process.dto.ConfigurationChanges;
import fr.plum.e2e.manager.core.application.command.synchronization.process.factory.SynchronizationErrorFactory;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.port.FileSynchronizationPort;
import fr.plum.e2e.manager.core.domain.port.repository.FileConfigurationRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ConfigurationSynchronizer {

  private final ClockPort clockPort;
  private final FileConfigurationRepositoryPort fileConfigurationRepositoryPort;
  private final FileSynchronizationPort fileSynchronizationPort;

  public void synchronizeConfigurations(
      EnvironmentId environmentId,
      Map<SynchronizationFileName, SynchronizationFileContent> processedFiles,
      List<SynchronizationError> errors) {
    try {
      var changes = analyzeConfigurations(environmentId, processedFiles, errors);

      if (!changes.toDelete().isEmpty()) {
        fileConfigurationRepositoryPort.delete(changes.toDelete());
      }
      if (!changes.toCreate().isEmpty()) {
        fileConfigurationRepositoryPort.save(changes.toCreate());
      }
      if (!changes.toUpdate().isEmpty()) {
        fileConfigurationRepositoryPort.update(changes.toUpdate());
      }
    } catch (Exception exception) {
      log.error(
          "Error during synchronization for Environment id [{}].",
          environmentId.value(),
          exception);
      errors.add(
          SynchronizationErrorFactory.createGlobalError(exception.getMessage(), clockPort.now()));
    }
  }

  private ConfigurationChanges analyzeConfigurations(
      EnvironmentId environmentId,
      Map<SynchronizationFileName, SynchronizationFileContent> processedFiles,
      List<SynchronizationError> errors) {

    var oldConfigurations = fileConfigurationRepositoryPort.findAll(environmentId);
    var newFileNames =
        processedFiles.keySet().stream()
            .map(SynchronizationFileName::value)
            .collect(Collectors.toSet());

    var toDelete =
        oldConfigurations.stream()
            .filter(oldFile -> !newFileNames.contains(oldFile.getId().value()))
            .collect(Collectors.toList());

    var toCreate = new ArrayList<FileConfiguration>();
    var toUpdate = new ArrayList<FileConfiguration>();

    for (var entry : processedFiles.entrySet()) {
      try {
        var oldConfig =
            oldConfigurations.stream()
                .filter(old -> old.hasFile(entry.getKey().value()))
                .findFirst();

        var newConfig =
            buildConfiguration(
                environmentId, entry.getKey(), entry.getValue(), oldConfig.orElse(null));

        if (newConfig.isEmpty() || newConfig.hasOnlyDisabledConfigurations()) {
          oldConfig.ifPresent(toDelete::add);
        } else if (oldConfig.isPresent()) {
          if (oldConfig.get().hasChanged(newConfig)) {
            toUpdate.add(newConfig);
          }
        } else {
          toCreate.add(newConfig);
        }
      } catch (CustomException e) {
        errors.add(
            SynchronizationErrorFactory.createFileError(
                entry.getKey(), e.getDescription(), clockPort.now()));
      } catch (Exception e) {
        errors.add(
            SynchronizationErrorFactory.createFileError(
                entry.getKey(), e.getMessage(), clockPort.now()));
      }
    }

    return new ConfigurationChanges(toDelete, toCreate, toUpdate);
  }

  private FileConfiguration buildConfiguration(
      EnvironmentId environmentId,
      SynchronizationFileName fileName,
      SynchronizationFileContent content,
      FileConfiguration oldConfig) {

    var newConfig = fileSynchronizationPort.buildFileConfiguration(fileName, content);
    newConfig.validateUniqueTitles();
    newConfig.removeDisabledConfigurations();

    if (oldConfig != null) {
      oldConfig.updateFrom(newConfig);
      return oldConfig;
    } else {
      newConfig.setEnvironmentId(environmentId);
      newConfig.initializeSuitesAndTestsIds();
      return newConfig;
    }
  }
}
