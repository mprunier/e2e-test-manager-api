package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestNotFoundException extends CustomException {

  public TestNotFoundException(Long id) {
    super(Response.Status.NOT_FOUND, "test-not-found", String.format("Test ID %s not found.", id));
  }
}
