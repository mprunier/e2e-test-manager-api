package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationAlreadyExistException extends CustomException {

    public ConfigurationAlreadyExistException(String path) {
        super(Response.Status.BAD_REQUEST,
              "configuration-already-exist",
              String.format("Configuration with path %s already exist.", path));
    }
}
