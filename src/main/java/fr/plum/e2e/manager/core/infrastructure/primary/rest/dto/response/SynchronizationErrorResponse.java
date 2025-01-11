package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record SynchronizationErrorResponse(
    @NotBlank String file, @NotBlank String error, @NotNull ZonedDateTime at) {
  public static List<SynchronizationErrorResponse> fromDomain(
      List<SynchronizationError> synchronizationErrors) {
    return synchronizationErrors.stream()
        .map(
            synchronizationError ->
                new SynchronizationErrorResponse(
                    synchronizationError.file().value(),
                    synchronizationError.error().value(),
                    synchronizationError.at()))
        .toList();
  }
}
