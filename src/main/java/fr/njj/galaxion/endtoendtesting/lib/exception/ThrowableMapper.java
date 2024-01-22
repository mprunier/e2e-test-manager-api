package fr.njj.galaxion.endtoendtesting.lib.exception;

import io.quarkus.logging.Log;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.apache.commons.lang3.StringUtils;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable exception) {
    ExceptionResponse exceptionResponse;
    Response.Status status;

    if (exception instanceof CustomException customException
        && StringUtils.isNotBlank(customException.getTitle())) {
      status = Response.Status.fromStatusCode(customException.getStatus());
      if (Response.Status.Family.SERVER_ERROR.equals(status.getFamily())) {
        Log.error(String.format("EXCEPTION <-!-> %s", exception));
      } else if (Response.Status.Family.CLIENT_ERROR.equals(status.getFamily())) {
        Log.warn(String.format("EXCEPTION <-!-> %s", exception));
      }
      exceptionResponse =
          new ExceptionResponse(
              customException.getStatus(),
              customException.getTitle(),
              customException.getDetail(),
              customException.getDescription(),
              null);
    } else {
      if (!(exception instanceof NotFoundException)
          && !(exception instanceof NotAllowedException)) {
        Log.error("EXCEPTION <-!-> ", exception);
      }
      status = Response.Status.INTERNAL_SERVER_ERROR;
      exceptionResponse =
          new ExceptionResponse(
              status.getStatusCode(),
              "internal-error",
              "An error has occurred, please try again later or contact our customer service.",
              null,
              null);
    }

    return Response.status(status)
        .entity(exceptionResponse)
        .type("application/problem+json")
        .build();
  }
}
