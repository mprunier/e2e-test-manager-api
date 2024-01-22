package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationTestIdentifierNotFoundException extends CustomException {

    public ConfigurationTestIdentifierNotFoundException(String identifier) {
        super(Response.Status.NOT_FOUND,
              "configuration-test-identifier-not-found",
              String.format("Configuration Test with identifier %s not found.", identifier));
    }
}
