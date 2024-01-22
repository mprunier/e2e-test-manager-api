package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSynchronizationResponse;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSynchronizationRetrievalService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/configurations/synchronizations")
@RequiredArgsConstructor
public class ConfigurationSyncController {

    private final ConfigurationSynchronizationRetrievalService configurationSynchronizationRetrievalService;

    @GET
    public ConfigurationSynchronizationResponse retrieve(@NotNull @QueryParam("environmentId") Long environmentId) {
        return configurationSynchronizationRetrievalService.getResponse(environmentId);
    }
}

