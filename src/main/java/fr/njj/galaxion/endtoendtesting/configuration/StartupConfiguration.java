package fr.njj.galaxion.endtoendtesting.configuration;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StartupConfiguration {

    @ConfigProperty(name = "quarkus.http.port")
    String serverPort;

    @ConfigProperty(name = "quarkus.application.version")
    String version;

    @ConfigProperty(name = "quarkus.application.name")
    String application;

    void onStart(@Observes StartupEvent e) {
        var baseUrl = "http://localhost:" + serverPort;
        log.info(String.format("\n================================================================================"
                               + "\n    Microservice: %s:%s"
                               + "\n    Base Url: %s"
                               + "\n    Swagger Url: %s/q/swagger-ui"
                               + "\n    Health Url: %s/q/health"
                               + "\n================================================================================",
                               application, version, baseUrl, baseUrl, baseUrl));
    }
}
