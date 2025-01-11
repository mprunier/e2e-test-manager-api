package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterSuite;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilterTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.worker.JpaWorkerEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.worker.JpaWorkerUnitEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.worker.WorkerUnitFilterDto;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkerMapper {

  public static Worker toDomain(JpaWorkerEntity entity) {
    return Worker.builder()
        .workerId(new WorkerId(entity.getId()))
        .environmentId(new EnvironmentId(entity.getEnvironmentId()))
        .type(entity.getType())
        .variables(
            entity.getVariables().entrySet().stream()
                .map(entry -> new WorkerVariable(entry.getKey(), entry.getValue()))
                .toList())
        .workerUnits(entity.getUnits().stream().map(WorkerMapper::toWorkerUnit).toList())
        .auditInfo(AuditInfoMapper.toDomain(entity))
        .build();
  }

  public static JpaWorkerEntity toEntity(Worker domain) {
    var entity =
        JpaWorkerEntity.builder()
            .id(domain.getId().value())
            .environmentId(domain.getEnvironmentId().value())
            .type(domain.getType())
            .variables(
                domain.getVariables().stream()
                    .collect(Collectors.toMap(WorkerVariable::name, WorkerVariable::value)))
            .build();

    entity.setUnits(
        domain.getWorkerUnits().stream().map(unit -> toWorkerUnitEntity(unit, entity)).toList());
    entity.setAuditFields(domain.getAuditInfo());
    return entity;
  }

  private static WorkerUnit toWorkerUnit(JpaWorkerUnitEntity entity) {
    return WorkerUnit.create(
        new WorkerUnitId(entity.getId()),
        entity.getStatus(),
        toWorkerUnitFilter(entity.getFilter()));
  }

  private static JpaWorkerUnitEntity toWorkerUnitEntity(WorkerUnit domain, JpaWorkerEntity worker) {
    return JpaWorkerUnitEntity.builder()
        .id(domain.getId().value())
        .status(domain.getStatus())
        .worker(worker)
        .filter(toWorkerUnitFilterDto(domain.getFilter()))
        .build();
  }

  private static WorkerUnitFilter toWorkerUnitFilter(WorkerUnitFilterDto dto) {
    if (dto == null) return null;

    return WorkerUnitFilter.builder()
        .fileNames(dto.getFileNames().stream().map(FileName::new).toList())
        .tag(dto.getTag() != null ? new Tag(dto.getTag()) : null)
        .suiteFilter(
            dto.getSuiteFilter() != null
                ? WorkerUnitFilterSuite.builder()
                    .suiteConfigurationId(
                        new SuiteConfigurationId(dto.getSuiteFilter().getSuiteConfigurationId()))
                    .suiteTitle(new SuiteTitle(dto.getSuiteFilter().getSuiteTitle()))
                    .build()
                : null)
        .testFilter(
            dto.getTestFilter() != null
                ? WorkerUnitFilterTest.builder()
                    .testConfigurationId(
                        new TestConfigurationId(dto.getTestFilter().getTestConfigurationId()))
                    .testTitle(new TestTitle(dto.getTestFilter().getTestTitle()))
                    .build()
                : null)
        .build();
  }

  private static WorkerUnitFilterDto toWorkerUnitFilterDto(WorkerUnitFilter domain) {
    if (domain == null) return null;

    return WorkerUnitFilterDto.builder()
        .fileNames(domain.fileNames().stream().map(FileName::value).toList())
        .tag(domain.tag() != null ? domain.tag().value() : null)
        .suiteFilter(
            domain.suiteFilter() != null
                ? WorkerUnitFilterDto.FilterSuiteDto.builder()
                    .suiteConfigurationId(domain.suiteFilter().suiteConfigurationId().value())
                    .suiteTitle(domain.suiteFilter().suiteTitle().value())
                    .build()
                : null)
        .testFilter(
            domain.testFilter() != null
                ? WorkerUnitFilterDto.FilterTestDto.builder()
                    .testConfigurationId(domain.testFilter().testConfigurationId().value())
                    .testTitle(domain.testFilter().testTitle().value())
                    .build()
                : null)
        .build();
  }
}
