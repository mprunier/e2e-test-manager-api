package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConcurrentJobsReachedException extends CustomException {

    public ConcurrentJobsReachedException() {
        super(Response.Status.BAD_REQUEST,
              "concurrent-job-reached",
              "Number of concurrent jobs reached. Please wait a few minutes.");
    }
}
