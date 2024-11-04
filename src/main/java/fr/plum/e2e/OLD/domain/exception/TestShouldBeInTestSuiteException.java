package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestShouldBeInTestSuiteException extends CustomException {

  public TestShouldBeInTestSuiteException() {
    super(
        Response.Status.BAD_REQUEST,
        "test-should-be-in-test-configuration",
        "Test should be in a test configuration.");
  }
}
