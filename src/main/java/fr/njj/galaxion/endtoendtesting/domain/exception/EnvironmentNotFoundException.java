package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentNotFoundException extends CustomException {

    public EnvironmentNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "environment-not-found",
              String.format("Environment ID %s not found.", id));
    }
}
