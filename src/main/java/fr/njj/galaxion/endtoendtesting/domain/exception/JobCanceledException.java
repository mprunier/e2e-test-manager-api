package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobCanceledException extends CustomException {

  public JobCanceledException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "job-canceled",
        String.format("Job with pipeline id %s canceled.", id));
  }
}
