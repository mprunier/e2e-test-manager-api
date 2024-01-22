package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationVariableNotFoundException extends CustomException {

    public ConfigurationVariableNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "configuration-variable-not-found",
              String.format("Configuration Variable ID %s not found.", id));
    }
}
