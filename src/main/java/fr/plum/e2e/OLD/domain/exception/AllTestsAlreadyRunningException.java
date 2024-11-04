package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class AllTestsAlreadyRunningException extends CustomException {

  public AllTestsAlreadyRunningException() {
    super(
        Response.Status.BAD_REQUEST,
        "all-scheduler-test-in-progress",
        "All the tests are already being played.");
  }
}
