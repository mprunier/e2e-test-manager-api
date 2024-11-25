package fr.plum.e2e.manager.core.domain.model.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record GetAllMetricsQuery(EnvironmentId environmentId, LocalDate sinceAt) {}
