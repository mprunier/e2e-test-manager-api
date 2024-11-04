package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobUnknownErrorException extends CustomException {

  public JobUnknownErrorException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "job-unknown-error",
        String.format("Job with worker id %s has an unknown error.", id));
  }
}
