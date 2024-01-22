package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class CharactersForbiddenException extends CustomException {

  public CharactersForbiddenException() {
    super(Response.Status.BAD_REQUEST, "characters-forbidden", "Characters |; are forbidden.");
  }
}
