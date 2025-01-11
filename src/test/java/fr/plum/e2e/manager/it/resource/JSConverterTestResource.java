package fr.plum.e2e.manager.it.resource;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class JSConverterTestResource implements QuarkusTestResourceLifecycleManager {

  private GenericContainer<?> converterContainer;

  @Override
  public Map<String, String> start() {
    converterContainer =
        new GenericContainer<>(DockerImageName.parse("maxpnr/js-converter-api"))
            .withExposedPorts(3000);
    converterContainer.start();

    Map<String, String> properties = new HashMap<>();
    properties.put(
        "quarkus.rest-client.converter.url",
        String.format("http://localhost:%d", converterContainer.getMappedPort(3000)));

    return properties;
  }

  @Override
  public void stop() {
    if (converterContainer != null) {
      converterContainer.stop();
    }
  }
}
