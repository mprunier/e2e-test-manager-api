package fr.plum.e2e.manager.sharedkernel.domain.model.aggregate;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public abstract class AggregateRoot<ID> extends Entity<ID> {
  protected AuditInfo auditInfo;

  public void createAuditInfo(ActionUsername username, ZonedDateTime now) {
    auditInfo = AuditInfo.create(username, now);
  }

  public void createAuditInfo(ZonedDateTime now) {
    auditInfo = AuditInfo.create(now);
  }

  public void updateAuditInfo(ActionUsername username, ZonedDateTime now) {
    auditInfo.update(username, now);
  }
}
