package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import java.util.ArrayList;
import java.util.List;
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
    this.sourceCodeInformation = sourceCodeInformation;
    this.maxParallelWorkers = mawWorkers;
  }

  public void updateVariables(List<EnvironmentVariable> newVariables) {
    variables.clear();
    variables.addAll(newVariables);
  }
}
