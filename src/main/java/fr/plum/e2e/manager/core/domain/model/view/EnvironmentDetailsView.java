package fr.plum.e2e.manager.core.domain.model.view;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record EnvironmentDetailsView(
    UUID id,
    String description,
    String projectId,
    String branch,
    String token,
    Boolean isEnabled,
    int maxParallelWorkers,
    boolean synchronizationInProgress,
    List<EnvironmentDetailsVariableView> variables,
    String createdBy,
    String updatedBy,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt) {

  public String getMaskedValue() {
    if (token.length() <= 6) return "**********";

    var masked = new StringBuilder(token);
    for (int i = 3; i < token.length() - 3; i++) {
      masked.setCharAt(i, '*');
    }
    return masked.toString();
  }
}
