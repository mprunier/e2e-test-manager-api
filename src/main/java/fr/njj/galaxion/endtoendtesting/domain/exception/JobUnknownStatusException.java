package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobUnknownStatusException extends CustomException {

  public JobUnknownStatusException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "job-unknown-status",
        String.format("Job with pipeline id %s has an unknown status.", id));
  }
}
