package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class RequiredVariableNotFoundException extends CustomException {

    public RequiredVariableNotFoundException(String variable) {
        super(Response.Status.NOT_FOUND,
              "required-variable-not-found",
              String.format("Required Variable [%s] not found.", variable));
    }
}
