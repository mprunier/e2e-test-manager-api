package fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.port.JavascriptConverterPort;

public class InMemoryJavascriptConverterAdapter implements JavascriptConverterPort {

  public static final String ERROR_TS = "error_ts";

  @Override
  public SynchronizationFileContent convertTsToJs(
      SynchronizationFileName fileName, SynchronizationFileContent content) {
    if (content.value().contains(ERROR_TS)) {
      throw new RuntimeException();
    }
    return content;
  }

  @Override
  public SynchronizationFileContent transpileJs(
      SynchronizationFileName fileName, SynchronizationFileContent content) {
    if (content.value().contains("error_es6")) {
      throw new RuntimeException();
    }
    return content;
  }
}
