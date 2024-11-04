package fr.plum.e2e.manager.core.infrastructure.secondary.external.converter.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileContent;
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
  public SynchronizationFileContent convertTsToJs(SynchronizationFileContent content) {
    log.info("Convert TS to JS");
    return new SynchronizationFileContent(converterClient.convertTs(content.value()));
  }

  @Override
  public SynchronizationFileContent transpileJs(SynchronizationFileContent content) {
    log.info("Transpile JS");
    return new SynchronizationFileContent(converterClient.transpileJs(content.value()));
  }
}
