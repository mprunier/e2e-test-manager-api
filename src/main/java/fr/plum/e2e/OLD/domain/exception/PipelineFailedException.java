package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class PipelineFailedException extends CustomException {

  public PipelineFailedException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "worker-failed",
        String.format("Pipeline id %s failed.", id));
  }
}
