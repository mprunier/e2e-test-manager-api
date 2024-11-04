package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class CharactersForbiddenException extends CustomException {

  public CharactersForbiddenException() {
    super(Response.Status.BAD_REQUEST, "characters-forbidden", "Characters |; are forbidden.");
  }
}
