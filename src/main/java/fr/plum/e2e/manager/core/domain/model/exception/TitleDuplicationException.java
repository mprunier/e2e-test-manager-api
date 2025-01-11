package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TitleDuplicationException extends CustomException {

  public TitleDuplicationException(String title) {
    super(
        Response.Status.BAD_REQUEST,
        "title-duplication",
        String.format(
            "You cannot have duplicate titles at the same level in the same file. Fix title '%s.'",
            title));
  }
}
