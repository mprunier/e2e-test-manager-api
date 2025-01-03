package fr.plum.e2e.manager.core.infrastructure.secondary.external.javascript.converter.client;

import fr.plum.e2e.manager.core.infrastructure.secondary.external.javascript.converter.exception.ConverterExceptionMapper;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "converter")
@RegisterProvider(ConverterExceptionMapper.class)
public interface ConverterClient {

  @POST
  @Path("/convert-ts")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/typescript")
  String convertTs(String typescriptCode);

  @POST
  @Path("/transpile-esnext")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes("text/plain")
  String transpileJs(String javascriptCode);
}
