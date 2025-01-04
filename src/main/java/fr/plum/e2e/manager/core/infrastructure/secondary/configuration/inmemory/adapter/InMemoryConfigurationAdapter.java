package fr.plum.e2e.manager.core.infrastructure.secondary.configuration.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;

public class InMemoryConfigurationAdapter implements ConfigurationPort {

  @Override
  public int getMaxJobInParallel() {
    return 2;
  }
}
