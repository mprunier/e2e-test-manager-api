package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.metrics.MetricsType;
import lombok.Builder;

@Builder
public record GetMetricsQuery(EnvironmentId environmentId, MetricsType metricsType) {}
