package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditInfo {
  private ActionUsername createdBy;
  private ZonedDateTime createdAt;
  private ActionUsername updatedBy;
  private ZonedDateTime updatedAt;

  public AuditInfo(
      ActionUsername createdBy,
      ZonedDateTime createdAt,
      ActionUsername updatedBy,
      ZonedDateTime updatedAt) {
    Assert.notNull("created by", createdBy);
    Assert.notNull("created at", createdAt);
    Assert.notNull("updated by", updatedBy);
    Assert.notNull("updated at", updatedAt);
    this.createdBy = createdBy;
    this.createdAt = createdAt;
    this.updatedBy = updatedBy;
    this.updatedAt = updatedAt;
  }

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
    Assert.notNull("updated by", updatedBy);
    Assert.notNull("updated at", updatedAt);
    this.updatedBy = updatedBy;
    this.updatedAt = updatedAt;
  }
}
