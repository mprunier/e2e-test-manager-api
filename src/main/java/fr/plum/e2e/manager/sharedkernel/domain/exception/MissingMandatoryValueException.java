package fr.plum.e2e.manager.sharedkernel.domain.exception;

import jakarta.ws.rs.core.Response;

public class MissingMandatoryValueException extends CustomException {

  public MissingMandatoryValueException(String field) {
    super(
        Response.Status.BAD_REQUEST,
        "missing-mandatory-value",
        String.format("The field %s is mandatory and cannot be empty or null.", field));
  }
}
