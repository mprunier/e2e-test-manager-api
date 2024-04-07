package fr.njj.galaxion.endtoendtesting.configuration;

import fr.njj.galaxion.endtoendtesting.usecases.environment.UnLockAllEnvironmentSynchronizationUseCase;
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

    private final UnLockAllEnvironmentSynchronizationUseCase unLockAllEnvironmentSynchronizationUseCase;

    void onStart(@Observes StartupEvent e) {
        var baseUrl = "http://localhost:" + serverPort;
        log.info(String.format("""
                                                                              \s
                                        ================================================================================
                                            Microservice: %s:%s
                                            Base Url: %s
                                            Swagger Url: %s/q/swagger-ui
                                            Health Url: %s/q/health
                                        ================================================================================
                                       \s""",
                               application, version, baseUrl, baseUrl, baseUrl));
        unLockAllEnvironmentSynchronizationUseCase.execute();
    }
}
