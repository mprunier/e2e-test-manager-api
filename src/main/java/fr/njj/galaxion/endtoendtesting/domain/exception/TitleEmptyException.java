package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TitleEmptyException extends CustomException {

    public TitleEmptyException(String file) {
        super(Response.Status.BAD_REQUEST,
              "title-empty",
              String.format("You cannot have empty title. Fix file '%s'", file));
    }
}
