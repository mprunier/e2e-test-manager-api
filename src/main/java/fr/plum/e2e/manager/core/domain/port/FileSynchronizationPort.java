package fr.plum.e2e.manager.core.domain.port;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import java.util.Map;

public interface FileSynchronizationPort {
  Map<SynchronizationFileName, SynchronizationFileContent> listFiles(
      SourceCodeProject sourceCodeProject);

  FileConfiguration buildFileConfiguration(
      EnvironmentId environmentId,
      SynchronizationFileName fileName,
      SynchronizationFileContent content);
}
