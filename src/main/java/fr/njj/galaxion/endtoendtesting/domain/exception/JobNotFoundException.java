package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobNotFoundException extends CustomException {

  public JobNotFoundException(String id) {
    super(Response.Status.NOT_FOUND, "job-not-found", String.format("Job ID %s not found.", id));
  }
}
