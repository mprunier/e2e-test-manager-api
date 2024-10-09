package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestShouldBeInTestSuiteException extends CustomException {

  public TestShouldBeInTestSuiteException() {
    super(
        Response.Status.BAD_REQUEST,
        "test-should-be-in-test-suite",
        "Test should be in a test suite.");
  }
}
