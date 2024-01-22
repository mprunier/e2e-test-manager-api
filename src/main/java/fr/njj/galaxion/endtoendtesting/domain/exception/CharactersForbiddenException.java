package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class CharactersForbiddenException extends CustomException {

    public CharactersForbiddenException(String file) {
        super(Response.Status.BAD_REQUEST,
              "characters-forbidden",
              String.format("Characters |; are forbidden. Fix file '%s'", file));
    }
}
