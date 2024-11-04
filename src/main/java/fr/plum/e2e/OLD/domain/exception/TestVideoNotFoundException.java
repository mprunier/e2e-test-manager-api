package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestVideoNotFoundException extends CustomException {

  public TestVideoNotFoundException() {
    super(Response.Status.NOT_FOUND, "test-video-not-found", "No video found for this test.");
  }
}
