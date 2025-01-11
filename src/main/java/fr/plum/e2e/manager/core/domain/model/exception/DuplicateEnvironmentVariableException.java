package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class DuplicateEnvironmentVariableException extends CustomException {
  public DuplicateEnvironmentVariableException(String variableName) {
    super(
        Response.Status.CONFLICT,
        "duplicate-environment-variable",
        String.format("Environment variable '%s' already exists.", variableName));
  }
}
