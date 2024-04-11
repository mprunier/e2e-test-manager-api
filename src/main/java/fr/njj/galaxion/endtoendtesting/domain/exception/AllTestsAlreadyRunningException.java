package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class AllTestsAlreadyRunningException extends CustomException {

    public AllTestsAlreadyRunningException() {
        super(Response.Status.BAD_REQUEST,
              "all-scheduler-test-in-progress",
              "All the tests are already being played.");
    }
}
