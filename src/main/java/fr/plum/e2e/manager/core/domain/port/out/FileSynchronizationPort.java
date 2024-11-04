package fr.plum.e2e.manager.core.domain.port.out;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import java.util.Map;

public interface FileSynchronizationPort {
  Map<SynchronizationFileName, SynchronizationFileContent> listFiles(
      SourceCodeProject sourceCodeProject);

  FileConfiguration buildFileConfiguration(
      SynchronizationFileName fileName, SynchronizationFileContent content);
}
