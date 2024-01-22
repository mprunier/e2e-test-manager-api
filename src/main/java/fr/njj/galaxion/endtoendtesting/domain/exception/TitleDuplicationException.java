package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TitleDuplicationException extends CustomException {

    public TitleDuplicationException(String file, String title) {
        super(Response.Status.BAD_REQUEST,
              "title-duplication",
              String.format("You cannot have duplicate titles at the same level in the same file. Fix file '%s', title '%s'", file, title));
    }
}
