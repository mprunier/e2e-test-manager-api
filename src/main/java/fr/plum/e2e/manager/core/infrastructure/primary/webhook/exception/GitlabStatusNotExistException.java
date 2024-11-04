package fr.plum.e2e.manager.core.infrastructure.primary.webhook.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class GitlabStatusNotExistException extends CustomException {

  public GitlabStatusNotExistException(String event) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "gitlab-status-not-exist",
        String.format("The gitlab status %s does not exist.", event));
  }
}
