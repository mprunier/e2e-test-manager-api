package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.view.TestResultScreenshotView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TestResultScreenshotResponse(@NotNull UUID id, @NotBlank String name) {

  public static TestResultScreenshotResponse fromTestResultScreenshotView(
      TestResultScreenshotView testResultScreenshotView) {
    return builder()
        .id(testResultScreenshotView.id())
        .name(testResultScreenshotView.name())
        .build();
  }
}
