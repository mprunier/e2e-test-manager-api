package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSchedulerResponse;
import fr.njj.galaxion.endtoendtesting.usecases.flowscheduler.RetrieveFlowSchedulerUseCase;
import io.quarkus.cache.CacheResult;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/configurations/schedulers")
@RequiredArgsConstructor
public class ConfigurationSchedulerController {

  private final RetrieveFlowSchedulerUseCase retrieveFlowSchedulerUseCase;

  @GET
  @CacheResult(cacheName = "schedulers")
  public ConfigurationSchedulerResponse retrieve(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveFlowSchedulerUseCase.execute(environmentId);
  }
}
