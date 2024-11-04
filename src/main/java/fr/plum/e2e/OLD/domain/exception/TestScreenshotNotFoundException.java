package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestScreenshotNotFoundException extends CustomException {

  public TestScreenshotNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "test-screenshot-not-found",
        String.format("Screenshot ID [%s] not found.", id));
  }
}
