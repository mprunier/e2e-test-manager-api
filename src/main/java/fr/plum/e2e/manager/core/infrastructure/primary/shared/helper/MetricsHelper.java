package fr.plum.e2e.manager.core.infrastructure.primary.shared.helper;

import fr.plum.e2e.manager.core.application.query.metrics.GetLastMetricsQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import fr.plum.e2e.manager.core.domain.model.exception.MetricsNotFoundException;
import fr.plum.e2e.manager.core.domain.model.query.GetMetricsQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.MetricsResponse;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class MetricsHelper {

  private final GetLastMetricsQueryHandler getLastMetricsQueryHandler;

  public MetricsResponse getLastMetrics(UUID environmentId) {
    var query = GetMetricsQuery.builder().environmentId(new EnvironmentId(environmentId)).build();
    var response = MetricsResponse.fromDomain(getLastMetricsQueryHandler.execute(query));
    if (!MetricsType.ALL.equals(response.getType())) {
      var secondQuery =
          GetMetricsQuery.builder()
              .environmentId(new EnvironmentId(environmentId))
              .metricsType(MetricsType.ALL)
              .build();
      try {
        var lastAllTestsRunAt =
            getLastMetricsQueryHandler.execute(secondQuery).getAuditInfo().getCreatedAt();
        response.addLastAllTestsRunAt(lastAllTestsRunAt);
      } catch (MetricsNotFoundException e) {
        // do nothing
      }
    }
    return response;
  }
}
