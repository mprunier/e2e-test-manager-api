package fr.njj.galaxion.endtoendtesting.client.tstojsconverter;

import fr.njj.galaxion.endtoendtesting.client.tstojsconverter.exception.TsToJsConverterExceptionMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "tsToJsConverter")
@RegisterProvider(TsToJsConverterExceptionMapper.class)
public interface TsToJsConverterClient {

    @POST
    @Path("/convert")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("text/typescript")
    String convert(String typescriptCode);

}

