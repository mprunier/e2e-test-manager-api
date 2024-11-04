package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentNotFoundException extends CustomException {

  public EnvironmentNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "environment-not-found",
        String.format("Environment ID %s not found.", id));
  }
}
