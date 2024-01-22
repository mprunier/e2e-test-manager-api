package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestInProgressException extends CustomException {

    public TestInProgressException() {
        super(Response.Status.BAD_REQUEST,
              "test-in-progress",
              "No test should be running to initiate a synchronization.");
    }
}
