package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.EnvironmentErrorResponse;
import fr.njj.galaxion.endtoendtesting.usecases.error.RetrieveErrorUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/errors")
@RequiredArgsConstructor
public class ErrorController {

    private final RetrieveErrorUseCase retrieveErrorUseCase;

    @GET
    public List<EnvironmentErrorResponse> retrieveErrors(@NotNull @QueryParam("environmentId") Long environmentId) {
        return retrieveErrorUseCase.execute(environmentId);
    }
}

