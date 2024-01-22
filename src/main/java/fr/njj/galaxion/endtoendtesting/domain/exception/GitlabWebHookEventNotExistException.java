package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class GitlabWebHookEventNotExistException extends CustomException {

  public GitlabWebHookEventNotExistException(String event) {
    super(
        Response.Status.BAD_REQUEST,
        "gitlab.webhook.event.not.exist",
        String.format("The gitlab event %s does not exist", event));
  }
}
