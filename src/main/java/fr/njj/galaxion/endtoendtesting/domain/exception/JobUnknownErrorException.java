package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class JobUnknownErrorException extends CustomException {

    public JobUnknownErrorException(String id) {
        super(Response.Status.INTERNAL_SERVER_ERROR,
              "job-unknown-error",
              String.format("Job with pipeline id %s has an unknown error.", id));
    }
}
