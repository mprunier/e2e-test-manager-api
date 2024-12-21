package fr.plum.e2e.manager.core.infrastructure.secondary.configuration.adapter;

import fr.plum.e2e.manager.core.domain.port.ConfigurationPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class QuarkusConfigurationAdapter implements ConfigurationPort {

  @ConfigProperty(name = "business.worker.max-unit-in-parallel")
  Integer maxJobInParallel;

  @Override
  public int getMaxJobInParallel() {
    return maxJobInParallel;
  }
}
