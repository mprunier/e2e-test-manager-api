package fr.plum.e2e.manager.core.domain.port.out;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;

public interface JavascriptConverterPort {
  SynchronizationFileContent convertTsToJs(SynchronizationFileContent content);

  SynchronizationFileContent transpileJs(SynchronizationFileContent content);
}
