package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestScreenshotNotFoundException extends CustomException {

    public TestScreenshotNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "test-screenshot-not-found",
              String.format("Screenshot on test ID %s not found.", id));
    }
}
