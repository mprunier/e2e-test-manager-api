package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.response.MetricsResponse;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.RetrieveFinalMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.RetrieveMetricsHistoryUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Path("/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final RetrieveMetricsHistoryUseCase retrieveMetricsHistoryUseCase;
    private final RetrieveFinalMetricsUseCase retrieveFinalMetricsUseCase;

    @GET
    @Path("/history")
    public List<MetricsResponse> retrieveMetricsHistory(@NotNull @QueryParam("environmentId") Long environmentId,
                                                        @NotNull @QueryParam("since") String sinceStr) {
        var since = LocalDate.parse(sinceStr);
        return retrieveMetricsHistoryUseCase.execute(environmentId, since);
    }

    @GET
    public MetricsResponse retrieveFinalMetrics(@NotNull @QueryParam("environmentId") Long environmentId) {
        return retrieveFinalMetricsUseCase.execute(environmentId);
    }
}

