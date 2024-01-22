package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationSuiteNotFoundException extends CustomException {

    public ConfigurationSuiteNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "configuration-suite-not-found",
              String.format("Configuration Suite with id %s not found.", id));
    }
}
