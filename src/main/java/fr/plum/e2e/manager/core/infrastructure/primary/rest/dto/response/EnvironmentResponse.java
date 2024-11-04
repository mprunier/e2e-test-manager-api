package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.EnvironmentView;
import java.util.List;
import java.util.UUID;

public record EnvironmentResponse(UUID id, String description) {

  public static List<EnvironmentResponse> from(List<EnvironmentView> environmentViews) {
    return environmentViews.stream()
        .map(
            environmentProjection ->
                new EnvironmentResponse(
                    environmentProjection.id(), environmentProjection.description()))
        .toList();
  }
}
