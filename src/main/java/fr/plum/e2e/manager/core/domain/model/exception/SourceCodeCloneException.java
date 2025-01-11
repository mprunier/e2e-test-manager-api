package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SourceCodeCloneException extends CustomException {
  public SourceCodeCloneException(String description) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "source-code-clone-exception",
        String.format("Source code error during clone repository. Error is : %s.", description));
  }
}
