package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class RequiredVariableNotFoundException extends CustomException {

  public RequiredVariableNotFoundException(String variable) {
    super(
        Response.Status.NOT_FOUND,
        "required-variable-not-found",
        String.format("Required Variable [%s] not found.", variable));
  }
}
