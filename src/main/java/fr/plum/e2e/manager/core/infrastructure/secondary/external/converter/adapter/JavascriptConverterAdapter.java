package fr.plum.e2e.manager.core.infrastructure.secondary.external.converter.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.port.out.JavascriptConverterPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.converter.client.ConverterClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class JavascriptConverterAdapter implements JavascriptConverterPort {

  @RestClient private ConverterClient converterClient;

  @Override
  public SynchronizationFileContent convertTsToJs(
      SynchronizationFileName fileName, SynchronizationFileContent content) {
    log.trace("Converting TS to JS for file [{}]...", fileName.value());
    try {
      var fileContent = converterClient.convertTs(content.value());
      return new SynchronizationFileContent(fileContent);
    } catch (Exception exception) {
      log.warn(
          "Error converting TS to JS for file [{}]: {}", fileName.value(), exception.getMessage());
      throw exception;
    }
  }

  @Override
  public SynchronizationFileContent transpileJs(
      SynchronizationFileName fileName, SynchronizationFileContent content) {
    log.trace("Transpiling JS for file [{}]...", fileName.value());
    try {
      var fileContent = converterClient.transpileJs(content.value());
      return new SynchronizationFileContent(fileContent);
    } catch (Exception exception) {
      log.warn("Error transpiling JS for file [{}]: {}", fileName.value(), exception.getMessage());
      throw exception;
    }
  }
}
