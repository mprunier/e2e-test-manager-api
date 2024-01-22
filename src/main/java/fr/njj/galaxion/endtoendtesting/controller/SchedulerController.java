package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.SchedulerResponse;
import fr.njj.galaxion.endtoendtesting.service.SchedulerRetrievalService;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

    private final SchedulerRetrievalService schedulerRetrievalService;

    @GET
    @Path("{id}")
    public SchedulerResponse getSchedulerResponse(@PathParam("id") Long id) {
        return schedulerRetrievalService.getSchedulerResponse(id);
    }

    @GET
    public List<SchedulerResponse> getSchedulerResponses(@NotNull @QueryParam("environmentId") Long environmentId) {
        return schedulerRetrievalService.getSchedulerResponses(environmentId);
    }
}

