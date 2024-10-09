package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SuiteShouldBeNotContainsSubSuiteException extends CustomException {

  public SuiteShouldBeNotContainsSubSuiteException() {
    super(
        Response.Status.BAD_REQUEST,
        "suite-should-be-not-contains-sub-suite",
        "Suite should be not contains sub suite.");
  }
}
