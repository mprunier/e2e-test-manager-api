package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.EnvironmentVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment.JpaEnvironmentEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.environment.JpaEnvironmentVariableEntity;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EnvironmentMapper {

  public static Environment toDomain(JpaEnvironmentEntity entity) {
    var sourceCodeInformation =
        SourceCodeInformation.builder()
            .projectId(entity.getProjectId())
            .token(entity.getToken())
            .branch(entity.getBranch())
            .build();
    var environment =
        Environment.builder()
            .environmentId(new EnvironmentId(entity.getId()))
            .environmentDescription(new EnvironmentDescription(entity.getDescription()))
            .sourceCodeInformation(sourceCodeInformation)
            .isEnabled(new EnvironmentIsEnabled(entity.isEnabled()))
            .maxParallelWorkers(new MaxParallelWorkers(entity.getMaxParallelTestNumber()))
            .auditInfo(AuditInfoMapper.toDomain(entity))
            .build();

    var variables = toVariableDomain(entity);
    environment.updateVariables(variables);
    return environment;
  }

  private static ArrayList<EnvironmentVariable> toVariableDomain(JpaEnvironmentEntity entity) {
    var variables = new ArrayList<EnvironmentVariable>();
    entity
        .getVariables()
        .forEach(
            varEntity ->
                variables.add(
                    EnvironmentVariable.builder()
                        .environmentVariableId(new EnvironmentVariableId(varEntity.getName()))
                        .value(new VariableValue(varEntity.getDefaultValue()))
                        .description(new VariableDescription(varEntity.getDescription()))
                        .isHidden(new VariableIsHidden(varEntity.isHidden()))
                        .build()));
    return variables;
  }

  public static JpaEnvironmentEntity toEntity(Environment domain) {
    var environment =
        JpaEnvironmentEntity.builder()
            .id(domain.getId().value())
            .description(domain.getEnvironmentDescription().value())
            .projectId(domain.getSourceCodeInformation().projectId())
            .token(domain.getSourceCodeInformation().token())
            .branch(domain.getSourceCodeInformation().branch())
            .isEnabled(domain.getIsEnabled().value())
            .maxParallelTestNumber(domain.getMaxParallelWorkers().value())
            .build();

    environment.setAuditFields(domain.getAuditInfo());

    var variables =
        domain.getVariables().stream().map(var -> toVariableEntity(var, environment)).toList();
    environment.setVariables(variables);

    return environment;
  }

  private static JpaEnvironmentVariableEntity toVariableEntity(
      EnvironmentVariable domain, JpaEnvironmentEntity environment) {
    return JpaEnvironmentVariableEntity.builder()
        .name(domain.getId().name())
        .environment(environment)
        .defaultValue(domain.getValue().value())
        .description(domain.getDescription().value())
        .isHidden(domain.getIsHidden().value())
        .build();
  }
}
