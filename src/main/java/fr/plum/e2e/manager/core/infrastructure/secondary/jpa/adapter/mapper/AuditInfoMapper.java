package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AuditInfo;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.AbstractAuditableEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuditInfoMapper {

  public static AuditInfo toDomain(AbstractAuditableEntity entity) {
    return AuditInfo.builder()
        .createdBy(new ActionUsername(entity.getCreatedBy()))
        .createdAt(entity.getCreatedAt())
        .updatedBy(entity.getUpdatedBy() != null ? new ActionUsername(entity.getUpdatedBy()) : null)
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
