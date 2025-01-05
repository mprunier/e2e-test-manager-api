package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.converter.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class ConverterExceptionMapper implements ResponseExceptionMapper<CustomException> {

  @Override
  public CustomException toThrowable(Response response) {
    int statusCode = response.getStatus();
    return new CustomException(
        Response.Status.fromStatusCode(statusCode),
        "tsToJsConverter-exception",
        "TsToJsConverter error");
  }
}
