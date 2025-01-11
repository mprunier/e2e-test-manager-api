package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SuiteShouldBeNotContainsSubSuiteException extends CustomException {

  public SuiteShouldBeNotContainsSubSuiteException() {
    super(
        Response.Status.BAD_REQUEST,
        "configuration-should-be-not-contains-sub-configuration",
        "Suite should be not contains sub suiteFilter.");
  }
}
