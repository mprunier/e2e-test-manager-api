package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class DomainAssertException extends CustomException {
  public DomainAssertException(String value) {
    super(Response.Status.INTERNAL_SERVER_ERROR, "domain-assert-error", value);
  }
}
