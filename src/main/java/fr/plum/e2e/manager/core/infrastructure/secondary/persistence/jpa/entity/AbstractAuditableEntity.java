package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity;

import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@MappedSuperclass
public abstract class AbstractAuditableEntity extends PanacheEntityBase {

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Setter
  @Column(name = "updated_at")
  private ZonedDateTime updatedAt;

  @Setter
  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Setter
  @Column(name = "updated_by")
  private String updatedBy;

  public void setAuditFields(AuditInfo auditInfo) {
    this.createdAt = auditInfo.getCreatedAt();
    this.updatedAt = auditInfo.getUpdatedAt();
    this.createdBy = auditInfo.getCreatedBy().value();
    this.updatedBy = auditInfo.getUpdatedBy() != null ? auditInfo.getUpdatedBy().value() : null;
  }
}
