package fr.njj.galaxion.endtoendtesting.client.gitlab.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class GitlabExceptionMapper implements ResponseExceptionMapper<CustomException> {

    @Override
    public CustomException toThrowable(Response response) {
        int statusCode = response.getStatus();
        GitlabExceptionResponse exceptionResponse;
        try {
            exceptionResponse = response.readEntity(GitlabExceptionResponse.class);
        } catch (ProcessingException e) {
            return new CustomException(Response.Status.INTERNAL_SERVER_ERROR,
                                       "internal-client-error",
                                       "An error has occurred, please try again later or contact our customer service.",
                                       "undetermined");
        }
        return new CustomException(Response.Status.fromStatusCode(statusCode),
                                   "gitlab-exception",
                                   "Gitlab: " + (exceptionResponse.getMessage() != null ? exceptionResponse.getMessage().toString() : "No Message"),
                                   "gitlab");
    }
}
