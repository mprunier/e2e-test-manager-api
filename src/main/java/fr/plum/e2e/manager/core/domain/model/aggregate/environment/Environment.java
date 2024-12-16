package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class Environment extends AggregateRoot<EnvironmentId> {

  private EnvironmentDescription environmentDescription;
  private SourceCodeInformation sourceCodeInformation;

  @Builder.Default
  private MaxParallelWorkers maxParallelWorkers = MaxParallelWorkers.defaultValue();

  @Builder.Default private EnvironmentIsEnabled isEnabled = EnvironmentIsEnabled.defaultStatus();

  @Builder.Default private List<EnvironmentVariable> variables = new ArrayList<>();

  public void updateGlobalInfo(
      EnvironmentDescription description,
      SourceCodeInformation sourceCodeInformation,
      MaxParallelWorkers mawWorkers) {
    this.environmentDescription = description;
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
    this.maxParallelWorkers = mawWorkers;
  }

  public void updateVariables(List<EnvironmentVariable> newVariables) {
    var existingVarsMap =
        variables.stream().collect(Collectors.toMap(EnvironmentVariable::getId, var -> var));

    var updatedVariables =
        newVariables.stream()
            .map(
                newVar -> {
                  EnvironmentVariable existingVar = existingVarsMap.get(newVar.getId());
                  return existingVar != null ? existingVar.updateFrom(newVar) : newVar;
                })
            .toList();

    variables.clear();
    variables.addAll(updatedVariables);
  }
}
