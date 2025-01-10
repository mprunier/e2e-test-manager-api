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
import java.util.Set;
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
    var newFileNames = extractNewFileNames(processedFiles);
    var toDelete = findConfigurationsToDelete(oldConfigurations, newFileNames, errors);

    var toCreate = new ArrayList<FileConfiguration>();
    var toUpdate = new ArrayList<FileConfiguration>();

    for (var entry : processedFiles.entrySet()) {
      try {
        var oldConfig =
            oldConfigurations.stream()
                .filter(old -> old.hasFile(entry.getKey().value()))
                .findFirst();

        var configurationResult =
            buildConfiguration(
                environmentId, entry.getKey(), entry.getValue(), oldConfig.orElse(null));

        if (configurationResult.isEmpty()
            || configurationResult.configuration().hasOnlyDisabledConfigurations()) {
          oldConfig.ifPresent(toDelete::add);
        } else if (oldConfig.isPresent()) {
          if (configurationResult.hasChanges()) {
            toUpdate.add(configurationResult.configuration());
          }
        } else {
          toCreate.add(configurationResult.configuration());
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

  private Set<String> extractNewFileNames(
      Map<SynchronizationFileName, SynchronizationFileContent> processedFiles) {
    return processedFiles.keySet().stream()
        .map(SynchronizationFileName::value)
        .collect(Collectors.toSet());
  }

  private List<FileConfiguration> findConfigurationsToDelete(
      List<FileConfiguration> oldConfigurations,
      Set<String> newFileNames,
      List<SynchronizationError> errors) {

    return oldConfigurations.stream()
        .filter(oldFile -> shouldDeleteConfiguration(oldFile, newFileNames, errors))
        .toList();
  }

  private boolean shouldDeleteConfiguration(
      FileConfiguration oldFile, Set<String> newFileNames, List<SynchronizationError> errors) {

    boolean isNotInNewFiles = !newFileNames.contains(oldFile.getId().value());
    boolean isNotInErrors =
        errors.stream()
            .map(error -> error.file().value())
            .noneMatch(errorFileName -> errorFileName.equals(oldFile.getId().value()));

    return isNotInNewFiles && isNotInErrors;
  }

  private record ConfigurationBuildResult(FileConfiguration configuration, boolean hasChanges) {
    public boolean isEmpty() {
      return configuration == null;
    }
  }

  private ConfigurationBuildResult buildConfiguration(
      EnvironmentId environmentId,
      SynchronizationFileName fileName,
      SynchronizationFileContent content,
      FileConfiguration oldConfig) {

    var newConfig =
        fileSynchronizationPort.buildFileConfiguration(environmentId, fileName, content);
    newConfig.validateUniqueTitles();
    newConfig.removeDisabledConfigurations();

    if (oldConfig != null) {
      boolean hasChanges = oldConfig.hasChanged(newConfig);
      if (hasChanges) {
        oldConfig.update(newConfig);
      }
      return new ConfigurationBuildResult(oldConfig, hasChanges);
    }

    return new ConfigurationBuildResult(newConfig, true);
  }
}
