package fr.plum.e2e.manager.core.domain.model.exception;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_SUITE;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SuiteNoTitleException extends CustomException {

  public SuiteNoTitleException() {
    super(
        Response.Status.BAD_REQUEST,
        "configuration-no-title",
        String.format("Suite with title '%s' is forbidden.", NO_SUITE));
  }
}
