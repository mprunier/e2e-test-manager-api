package fr.plum.e2e.manager.core.infrastructure.primary.rest;

import fr.plum.e2e.manager.core.application.MetricsFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.query.GetAllMetricsQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.shared.helper.MetricsHelper;
import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "MetricsApi")
@Slf4j
@Authenticated
@Path("/auth/metrics")
@RequiredArgsConstructor
public class MetricsResource {

  private final MetricsFacade metricsFacade;
  private final MetricsHelper metricsHelper;

  @Operation(operationId = "getAll")
  @GET
  @Path("/history")
  public List<MetricsResponse> getAllMetrics(
      @NotNull @QueryParam("environmentId") UUID environmentId,
      @NotNull @QueryParam("since") String sinceStr) {
    var since = LocalDate.parse(sinceStr);
    var query =
        GetAllMetricsQuery.builder()
            .environmentId(new EnvironmentId(environmentId))
            .sinceAt(since)
            .build();
    return MetricsResponse.fromDomain(metricsFacade.getAllMetrics(query));
  }

  @Operation(operationId = "getLast")
  @GET
  @Path("/last")
  public MetricsResponse getLastMetrics(@NotNull @QueryParam("environmentId") UUID environmentId) {
    return metricsHelper.getLastMetrics(environmentId);
  }
}
