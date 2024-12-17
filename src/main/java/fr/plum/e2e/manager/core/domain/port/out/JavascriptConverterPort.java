package fr.plum.e2e.manager.core.domain.port.out;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;

public interface JavascriptConverterPort {
  SynchronizationFileContent convertTsToJs(
      SynchronizationFileName fileName, SynchronizationFileContent content);

  SynchronizationFileContent transpileJs(
      SynchronizationFileName fileName, SynchronizationFileContent content);
}
