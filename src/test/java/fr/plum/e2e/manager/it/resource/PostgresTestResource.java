package fr.plum.e2e.manager.it.resource;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.HashMap;
import java.util.Map;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

  private PostgreSQLContainer<?> postgreSQLContainer;

  @Override
  public Map<String, String> start() {
    postgreSQLContainer =
        new PostgreSQLContainer<>(DockerImageName.parse("postgres:15-alpine"))
            .withExposedPorts(5432)
            .withDatabaseName("quarkus")
            .withUsername("quarkus")
            .withPassword("quarkus");
    postgreSQLContainer.start();

    Map<String, String> properties = new HashMap<>();
    properties.put("quarkus.datasource.jdbc.url", postgreSQLContainer.getJdbcUrl());
    properties.put("quarkus.datasource.username", postgreSQLContainer.getUsername());
    properties.put("quarkus.datasource.password", postgreSQLContainer.getPassword());
    return properties;
  }

  @Override
  public void stop() {
    if (postgreSQLContainer != null) {
      postgreSQLContainer.stop();
    }
  }
}
