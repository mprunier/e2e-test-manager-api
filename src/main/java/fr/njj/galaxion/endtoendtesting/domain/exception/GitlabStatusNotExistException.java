package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class GitlabStatusNotExistException extends CustomException {

  public GitlabStatusNotExistException(String event) {
    super(
        Response.Status.BAD_REQUEST,
        "gitlab.status.not.exist",
        String.format("The gitlab status %s does not exist", event));
  }
}
