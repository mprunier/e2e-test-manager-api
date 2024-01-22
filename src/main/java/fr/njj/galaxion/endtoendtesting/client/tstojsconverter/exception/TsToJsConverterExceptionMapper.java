package fr.njj.galaxion.endtoendtesting.client.tstojsconverter.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class TsToJsConverterExceptionMapper implements ResponseExceptionMapper<CustomException> {

    @Override
    public CustomException toThrowable(Response response) {
        int statusCode = response.getStatus();
        return new CustomException(Response.Status.fromStatusCode(statusCode),
                                   "tsToJsConverter-exception",
                                   "TsToJsConverter error");
    }
}
