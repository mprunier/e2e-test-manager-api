package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class RunParameterException extends CustomException {

  public RunParameterException() {
    super(
        Response.Status.BAD_REQUEST,
        "run-parameter-exception",
        "Only configuration test ID or suite ID");
  }
}
