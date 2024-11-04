package fr.plum.e2e.OLD.controller;

import fr.plum.e2e.OLD.domain.response.MetricsResponse;
import fr.plum.e2e.OLD.usecases.metrics.RetrieveFinalMetricsUseCase;
import fr.plum.e2e.OLD.usecases.metrics.RetrieveMetricsHistoryUseCase;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/metrics")
@RequiredArgsConstructor
public class MetricController {

  private final RetrieveMetricsHistoryUseCase retrieveMetricsHistoryUseCase;
  private final RetrieveFinalMetricsUseCase retrieveFinalMetricsUseCase;

  @GET
  @Path("/history")
  public List<MetricsResponse> retrieveMetricsHistory(
      @NotNull @QueryParam("environmentId") Long environmentId,
      @NotNull @QueryParam("since") String sinceStr) {
    var since = LocalDate.parse(sinceStr);
    return retrieveMetricsHistoryUseCase.execute(environmentId, since);
  }

  @GET
  public MetricsResponse retrieveFinalMetrics(
      @NotNull @QueryParam("environmentId") Long environmentId) {
    return retrieveFinalMetricsUseCase.execute(environmentId);
  }
}
