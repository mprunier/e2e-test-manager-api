package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AuditInfo;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.Synchronization;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationErrorValue;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationFileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationIsInProgress;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.synchronization.JpaSynchronizationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.synchronization.JpaSynchronizationErrorEntity;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SynchronizationMapper {

  public static Synchronization toDomain(JpaSynchronizationEntity entity) {
    var errors = toErrorDomain(entity);

    return Synchronization.builder()
        .id(new EnvironmentId(entity.getEnvironmentId()))
        .synchronizationIsInProgress(new SynchronizationIsInProgress(entity.isInProgress()))
        .errors(errors)
        .auditInfo(
            AuditInfo.builder()
                .createdBy(new ActionUsername(entity.getCreatedBy()))
                .createdAt(entity.getCreatedAt())
                .updatedBy(new ActionUsername(entity.getUpdatedBy()))
                .updatedAt(entity.getUpdatedAt())
                .build())
        .build();
  }

  private static ArrayList<SynchronizationError> toErrorDomain(JpaSynchronizationEntity entity) {
    var errors = new ArrayList<SynchronizationError>();
    entity
        .getErrors()
        .forEach(
            errorEntity ->
                errors.add(
                    SynchronizationError.builder()
                        .file(new SynchronizationFileName(errorEntity.getFile()))
                        .error(new SynchronizationErrorValue(errorEntity.getError()))
                        .at(errorEntity.getAt())
                        .build()));
    return errors;
  }

  public static JpaSynchronizationEntity toEntity(Synchronization domain) {
    var synchronization =
        JpaSynchronizationEntity.builder()
            .environmentId(domain.getId().value())
            .isInProgress(domain.isInProgress())
            .build();

    synchronization.setAuditFields(
        domain.getAuditInfo().getCreatedAt(),
        domain.getAuditInfo().getUpdatedAt(),
        domain.getAuditInfo().getCreatedBy().value(),
        domain.getAuditInfo().getUpdatedBy() != null
            ? domain.getAuditInfo().getUpdatedBy().value()
            : null);

    var errors = toErrorEntity(domain, synchronization);

    synchronization.setErrors(errors);

    return synchronization;
  }

  private static List<JpaSynchronizationErrorEntity> toErrorEntity(
      Synchronization domain, JpaSynchronizationEntity synchronization) {
    var errors =
        domain.getErrors().stream()
            .map(
                error ->
                    JpaSynchronizationErrorEntity.builder()
                        .synchronization(synchronization)
                        .file(error.file().value())
                        .error(error.error().value())
                        .at(error.at())
                        .build())
            .toList();
    return errors;
  }
}
