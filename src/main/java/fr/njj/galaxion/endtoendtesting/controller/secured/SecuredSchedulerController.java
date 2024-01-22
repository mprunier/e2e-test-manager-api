package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.service.CancelSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.RunSchedulerService;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
@Path("/auth/scheduler")
@RequiredArgsConstructor
public class SecuredSchedulerController {

    private final CancelSchedulerService cancelSchedulerService;
    private final RunSchedulerService runSchedulerService;
    private final SecurityIdentity identity;

    @POST
    public void run(@NotNull @QueryParam("environmentId") Long environmentId) {
        var createdBy = identity != null && identity.getPrincipal() != null ? identity.getPrincipal().getName() : "Unknown";
        runSchedulerService.runFromUser(environmentId, createdBy);
    }

    @DELETE
    @Path("{id}")
    public void cancel(@PathParam("id") Long id) {
        cancelSchedulerService.cancel(id);
    }
}

