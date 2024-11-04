package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SubSuiteException extends CustomException {

  public SubSuiteException(String file, String title) {
    super(
        Response.Status.BAD_REQUEST,
        "sub-configuration-exist",
        String.format(
            "You cannot have sub configuration in configuration. Fix file '%s', Suite '%s'",
            file, title));
  }
}
