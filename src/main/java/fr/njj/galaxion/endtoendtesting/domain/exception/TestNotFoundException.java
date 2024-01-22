package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestNotFoundException extends CustomException {

    public TestNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "test-not-found",
              String.format("Test ID %s not found.", id));
    }
}
