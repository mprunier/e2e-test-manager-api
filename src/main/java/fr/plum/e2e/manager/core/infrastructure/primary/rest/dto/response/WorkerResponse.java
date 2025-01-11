package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.projection.WorkerProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record WorkerResponse(
    @NotNull UUID id,
    @NotBlank String createdBy,
    @NotNull ZonedDateTime createdAt,
    @NotNull WorkerType type) {
  public static WorkerResponse fromDomain(WorkerProjection domain) {
    return new WorkerResponse(domain.id(), domain.createdBy(), domain.createdAt(), domain.type());
  }

  public static List<WorkerResponse> fromDomain(List<WorkerProjection> domains) {
    return domains.stream().map(WorkerResponse::fromDomain).toList();
  }
}
