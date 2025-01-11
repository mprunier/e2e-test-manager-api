package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class MoreOneJobException extends CustomException {

  public MoreOneJobException(String id) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "more-one-job",
        String.format("More one job with worker on pipeline id [%s].", id));
  }
}
