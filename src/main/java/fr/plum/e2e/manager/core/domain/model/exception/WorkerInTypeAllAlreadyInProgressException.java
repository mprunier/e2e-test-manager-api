package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class WorkerInTypeAllAlreadyInProgressException extends CustomException {

  public WorkerInTypeAllAlreadyInProgressException() {
    super(
        Response.Status.BAD_REQUEST,
        "worker-in-type-all-in-progress",
        "All the tests are already being played.");
  }
}
