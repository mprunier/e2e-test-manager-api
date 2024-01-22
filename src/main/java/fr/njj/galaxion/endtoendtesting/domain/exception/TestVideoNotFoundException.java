package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestVideoNotFoundException extends CustomException {

    public TestVideoNotFoundException() {
        super(Response.Status.NOT_FOUND,
              "test-video-not-found",
              "No video found for this test.");
    }
}
