package fr.njj.galaxion.endtoendtesting.controller.secured;

import fr.njj.galaxion.endtoendtesting.domain.request.RunTestOrSuiteRequest;
import fr.njj.galaxion.endtoendtesting.usecases.run.RunTestUseCase;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

@Slf4j
@Authenticated
@Path("/auth/tests")
@RequiredArgsConstructor
public class SecuredTestController {

  private final RunTestUseCase runTestUseCase;

  @POST
  public void run(@RequestBody RunTestOrSuiteRequest request) {
    runTestUseCase.execute(request);
  }
}
