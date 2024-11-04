package fr.plum.e2e.manager.core.infrastructure.primary.webhook.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class GitlabWebHookEventNotExistException extends CustomException {

  public GitlabWebHookEventNotExistException(String event) {
    super(
        Response.Status.BAD_REQUEST,
        "gitlab-webhook-consumer-not-exist",
        String.format("The gitlab consumer %s does not exist", event));
  }
}
