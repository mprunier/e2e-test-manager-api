package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class HiddenVariableException extends CustomException {

  public HiddenVariableException() {
    super(
        Response.Status.BAD_REQUEST,
        "variable-hidden",
        "You cannot switch the value of a hidden variable. You can delete it, save, and then re-add it to disable the fact that it is hidden.");
  }
}
