package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.usecases.pipeline.CancelPipelineUseCase;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
@Path("/auth/pipelines")
@RequiredArgsConstructor
public class SecuredPipelineController {

  private final SecurityIdentity identity;

  private final CancelPipelineUseCase cancelPipelineUseCase;

  @DELETE
  @Path("{id}")
  public void cancel(@PathParam("id") String id) {
    var createdBy =
        identity != null && identity.getPrincipal() != null
            ? identity.getPrincipal().getName()
            : "Unknown";
    log.info("[{}] cancel a pipeline.", createdBy);
    cancelPipelineUseCase.execute(id, false);
  }
}
