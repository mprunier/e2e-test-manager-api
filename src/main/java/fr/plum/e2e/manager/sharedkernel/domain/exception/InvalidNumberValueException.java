package fr.plum.e2e.manager.sharedkernel.domain.exception;

import jakarta.ws.rs.core.Response;

public class InvalidNumberValueException extends CustomException {

  public InvalidNumberValueException(String message) {
    super(Response.Status.BAD_REQUEST, "invalid-number-value", message);
  }

  public static InvalidNumberValueException strictlyPositive(String field, Integer value) {
    return new InvalidNumberValueException(
        "Value of field " + field + " must be strictly positive, actual value is " + value);
  }

  public static InvalidNumberValueException positive(String field, Integer value) {
    return new InvalidNumberValueException(
        "Value of field " + field + " must be positive, actual value is " + value);
  }

  public static InvalidNumberValueException zero(String field, Integer value) {
    return new InvalidNumberValueException(
        "Value of field " + field + " must be 0, actual value is " + value);
  }
}
