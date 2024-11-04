package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TitleEmptyException extends CustomException {

  public TitleEmptyException() {
    super(Response.Status.BAD_REQUEST, "title-empty", "You cannot have empty title.");
  }
}
