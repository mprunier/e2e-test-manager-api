package fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.port.FileSynchronizationPort;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFileSynchronizationAdapter implements FileSynchronizationPort {

  private final Map<SourceCodeProject, Map<SynchronizationFileName, SynchronizationFileContent>>
      projectFiles = new HashMap<>();
  private final Map<String, FileConfiguration> fileConfigurations = new HashMap<>();

  @Override
  public Map<SynchronizationFileName, SynchronizationFileContent> listFiles(
      SourceCodeProject sourceCodeProject) {
    return projectFiles.computeIfAbsent(sourceCodeProject, k -> new HashMap<>());
  }

  @Override
  public FileConfiguration buildFileConfiguration(
      EnvironmentId environmentId,
      SynchronizationFileName fileName,
      SynchronizationFileContent content) {
    var key = buildKey(environmentId, fileName);
    return fileConfigurations.computeIfAbsent(
        key,
        k ->
            FileConfiguration.builder()
                .fileName(new FileName(fileName.value()))
                .environmentId(environmentId)
                .auditInfo(AuditInfo.create(new ActionUsername("System"), ZonedDateTime.now()))
                .suites(
                    new ArrayList<>(
                        List.of(
                            SuiteConfiguration.create(
                                new SuiteTitle("Default Suite"), new ArrayList<>(),
                                new ArrayList<>(),
                                    new ArrayList<>(
                                        List.of(
                                            TestConfiguration.create(
                                                new TestTitle("Default Test"),
                                                new Position(1),
                                                new ArrayList<>(),
                                                new ArrayList<>())))))))
                .build());
  }

  public void addFile(
      SourceCodeProject project,
      SynchronizationFileName fileName,
      SynchronizationFileContent content) {
    var files = projectFiles.computeIfAbsent(project, k -> new HashMap<>());
    files.put(fileName, content);
  }

  private String buildKey(EnvironmentId environmentId, SynchronizationFileName fileName) {
    return String.format("%s-%s", environmentId.value(), fileName.value());
  }
}
