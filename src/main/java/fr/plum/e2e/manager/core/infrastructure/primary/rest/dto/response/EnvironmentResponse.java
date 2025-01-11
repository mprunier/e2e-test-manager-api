package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.projection.EnvironmentProjection;
import java.util.List;
import java.util.UUID;

public record EnvironmentResponse(UUID id, String description) {

  public static List<EnvironmentResponse> from(List<EnvironmentProjection> environmentProjections) {
    return environmentProjections.stream()
        .map(
            environmentProjection ->
                new EnvironmentResponse(
                    environmentProjection.id(), environmentProjection.description()))
        .toList();
  }
}
