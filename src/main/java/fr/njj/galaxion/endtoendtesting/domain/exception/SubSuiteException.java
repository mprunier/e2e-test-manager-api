package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SubSuiteException extends CustomException {

    public SubSuiteException(String file, String title) {
        super(Response.Status.BAD_REQUEST,
              "sub-suite-exist",
              String.format("You cannot have sub suite in suite. Fix file '%s', Suite '%s'", file, title));
    }
}
