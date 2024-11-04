package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobNotFoundException extends CustomException {

  public JobNotFoundException(String id) {
    super(Response.Status.NOT_FOUND, "job-not-found", String.format("Job ID %s not found.", id));
  }
}
