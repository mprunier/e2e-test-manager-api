package fr.plum.e2e.manager.core.infrastructure.primary.shared;

import fr.plum.e2e.manager.core.application.MetricsFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.query.GetMetricsQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MetricsHelper {

  private final MetricsFacade metricsFacade;

  public MetricsResponse getLastMetrics(UUID environmentId) {
    var query = GetMetricsQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var response = MetricsResponse.fromDomain(metricsFacade.getLastMetrics(query));
    if (!MetricsType.ALL.equals(response.getType())) {
      var secondQuery =
          GetMetricsQuery.builder()
              .environmentId(new EnvironmentId(environmentId))
              .metricsType(MetricsType.ALL)
              .build();
      var lastAllTestsRunAt =
          metricsFacade.getLastMetrics(secondQuery).getAuditInfo().getCreatedAt();
      response.addLastAllTestsRunAt(lastAllTestsRunAt);
    }
    return response;
  }
}
