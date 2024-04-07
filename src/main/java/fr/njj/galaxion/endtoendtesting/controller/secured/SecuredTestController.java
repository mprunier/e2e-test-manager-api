package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.request.RunTestOrSuiteRequest;
import fr.njj.galaxion.endtoendtesting.service.CancelSuiteOrTestService;
import fr.njj.galaxion.endtoendtesting.service.RunSuiteOrTestService;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/tests")
@RequiredArgsConstructor
public class SecuredTestController {

    private final RunSuiteOrTestService runSuiteOrTestService;
    private final CancelSuiteOrTestService cancelSuiteOrTestService;

    @POST
    public void run(@NotNull @QueryParam("environmentId") Long environmentId,
                    @RequestBody RunTestOrSuiteRequest request) {
        runSuiteOrTestService.run(environmentId, request);
    }

    @DELETE
    @Path("{id}")
    public void cancel(@PathParam("id") Long id) {
        cancelSuiteOrTestService.cancel(id);
    }
}

