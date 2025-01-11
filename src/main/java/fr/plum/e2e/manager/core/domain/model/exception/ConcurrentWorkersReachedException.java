package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConcurrentWorkersReachedException extends CustomException {

  public ConcurrentWorkersReachedException() {
    super(
        Response.Status.BAD_REQUEST,
        "concurrent-worker-reached",
        "Number of concurrent worker units reached. Please wait a few minutes.");
  }
}
