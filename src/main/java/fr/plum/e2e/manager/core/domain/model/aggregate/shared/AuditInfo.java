package fr.plum.e2e.manager.core.domain.model.aggregate.shared;

import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AuditInfo {
  private final ActionUsername createdBy;
  private final ZonedDateTime createdAt;
  private ActionUsername updatedBy;
  private ZonedDateTime updatedAt;

  public static AuditInfo create(ZonedDateTime createdAt) {
    return builder()
        .createdBy(new ActionUsername("System"))
        .createdAt(createdAt)
        .updatedBy(new ActionUsername("System"))
        .updatedAt(createdAt)
        .build();
  }

  public static AuditInfo create(ActionUsername createdBy, ZonedDateTime createdAt) {
    return builder()
        .createdBy(createdBy)
        .createdAt(createdAt)
        .updatedBy(createdBy)
        .updatedAt(createdAt)
        .build();
  }

  public void update(ActionUsername updatedBy, ZonedDateTime updatedAt) {
    this.updatedBy = updatedBy;
    this.updatedAt = updatedAt;
  }
}
