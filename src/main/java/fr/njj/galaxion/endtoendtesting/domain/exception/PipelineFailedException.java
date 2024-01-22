package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class PipelineFailedException extends CustomException {

  public PipelineFailedException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "pipeline-failed",
        String.format("Pipeline id %s failed.", id));
  }
}
