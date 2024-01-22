package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class MoreOneJobException extends CustomException {

    public MoreOneJobException(String id) {
        super(Response.Status.INTERNAL_SERVER_ERROR,
              "more-one-job",
              String.format("ore one job with pipeline %s.", id));
    }
}
