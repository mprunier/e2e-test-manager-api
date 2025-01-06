package fr.plum.e2e.manager.core.infrastructure.secondary.configuration.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;

public class InMemoryConfigurationAdapter implements ConfigurationPort {

  private static int MAX_JOB_IN_PARALLEL = 2;

  @Override
  public int getMaxJobInParallel() {
    return MAX_JOB_IN_PARALLEL;
  }

  public void updateMaxJobInParallel(int maxJobInParallel) {
    MAX_JOB_IN_PARALLEL = maxJobInParallel;
  }
}
