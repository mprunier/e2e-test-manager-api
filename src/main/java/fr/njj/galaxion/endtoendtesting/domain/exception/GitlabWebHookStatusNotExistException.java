package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class GitlabWebHookStatusNotExistException extends CustomException {

    public GitlabWebHookStatusNotExistException(String event) {
        super(Response.Status.BAD_REQUEST,
              "gitlab.webhook.status.not.exist",
              String.format("The gitlab status %s does not exist", event));
    }
}
