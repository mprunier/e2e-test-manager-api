package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.AbstractAuditableEntity;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuditInfoMapper {

  public static AuditInfo toDomain(AbstractAuditableEntity entity) {
    return AuditInfo.builder()
        .createdBy(new ActionUsername(entity.getCreatedBy()))
        .createdAt(entity.getCreatedAt())
        .updatedBy(
            entity.getUpdatedBy() != null
                ? new ActionUsername(entity.getUpdatedBy())
                : new ActionUsername(entity.getCreatedBy()))
        .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : entity.getCreatedAt())
        .build();
  }
}
