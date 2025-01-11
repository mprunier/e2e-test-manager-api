package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Environment extends AggregateRoot<EnvironmentId> {

  private EnvironmentDescription environmentDescription;
  private SourceCodeInformation sourceCodeInformation;
  private MaxParallelWorkers maxParallelWorkers;
  private EnvironmentIsEnabled isEnabled;
  private List<EnvironmentVariable> variables;

  @Builder
  public Environment(
      EnvironmentId environmentId,
      AuditInfo auditInfo,
      EnvironmentDescription environmentDescription,
      SourceCodeInformation sourceCodeInformation,
      MaxParallelWorkers maxParallelWorkers,
      EnvironmentIsEnabled isEnabled,
      List<EnvironmentVariable> variables) {
    super(environmentId, auditInfo);
    Assert.notNull("environmentDescription", environmentDescription);
    Assert.notNull("sourceCodeInformation", sourceCodeInformation);
    Assert.notNull("maxParallelWorkers", maxParallelWorkers);
    Assert.notNull("isEnabled", isEnabled);
    Assert.notNull("variables", variables);
    this.environmentDescription = environmentDescription;
    this.sourceCodeInformation = sourceCodeInformation;
    this.maxParallelWorkers = maxParallelWorkers;
    this.isEnabled = isEnabled;
    this.variables = variables;
  }

  public static Environment create(
      EnvironmentDescription description,
      SourceCodeInformation sourceCodeInformation,
      List<EnvironmentVariable> variables,
      AuditInfo auditInfo) {
    return builder()
        .environmentId(EnvironmentId.generate())
        .environmentDescription(description)
        .sourceCodeInformation(sourceCodeInformation)
        .maxParallelWorkers(MaxParallelWorkers.defaultValue())
        .isEnabled(EnvironmentIsEnabled.defaultStatus())
        .auditInfo(auditInfo)
        .variables(variables)
        .build();
  }

  public void updateGlobalInfo(
      EnvironmentDescription environmentDescription,
      SourceCodeInformation sourceCodeInformation,
      MaxParallelWorkers maxParallelWorkers) {
    Assert.notNull("environmentDescription", environmentDescription);
    Assert.notNull("sourceCodeInformation", sourceCodeInformation);
    Assert.notNull("maxParallelWorkers", maxParallelWorkers);
    this.environmentDescription = environmentDescription;
    if (sourceCodeInformation.token().contains("****")) {
      this.sourceCodeInformation =
          SourceCodeInformation.builder()
              .projectId(sourceCodeInformation.projectId())
              .token(this.sourceCodeInformation.token())
              .branch(sourceCodeInformation.branch())
              .build();
    } else {
      this.sourceCodeInformation = sourceCodeInformation;
    }
    this.maxParallelWorkers = maxParallelWorkers;
  }

  public void updateVariables(List<EnvironmentVariable> newVariables) {
    Assert.notNull("variables", variables);
    var existingVarsMap =
        variables.stream().collect(Collectors.toMap(EnvironmentVariable::getId, var -> var));

    var updatedVariables =
        newVariables.stream()
            .map(
                newVar -> {
                  EnvironmentVariable existingVar = existingVarsMap.get(newVar.getId());
                  return existingVar != null ? existingVar.update(newVar) : newVar;
                })
            .toList();

    variables.clear();
    variables.addAll(updatedVariables);
  }
}
