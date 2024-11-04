package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class RunParameterException extends CustomException {

  public RunParameterException() {
    super(
        Response.Status.BAD_REQUEST,
        "run-parameter-exception",
        "Only configuration test ID or configuration ID");
  }
}
