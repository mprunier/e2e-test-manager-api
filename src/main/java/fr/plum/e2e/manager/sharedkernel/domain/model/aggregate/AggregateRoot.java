package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import java.time.ZonedDateTime;
import lombok.Getter;

@Getter
public abstract class AggregateRoot<ID> extends Entity<ID> {
  protected AuditInfo auditInfo;

  protected AggregateRoot(ID id, AuditInfo auditInfo) {
    super(id);
    Assert.notNull("AuditInfo must not be null for class " + getClass().getSimpleName(), auditInfo);
    this.auditInfo = auditInfo;
  }

  public void updateAuditInfo(ActionUsername username, ZonedDateTime now) {
    auditInfo.update(username, now);
  }
}
