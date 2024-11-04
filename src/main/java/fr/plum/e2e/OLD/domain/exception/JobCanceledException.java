package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobCanceledException extends CustomException {

  public JobCanceledException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "job-canceled",
        String.format("Job with worker id %s canceled.", id));
  }
}
