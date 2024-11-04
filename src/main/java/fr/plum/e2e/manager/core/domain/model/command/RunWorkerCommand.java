package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record RunWorkerCommand(
    EnvironmentId environmentId,
    ActionUsername username,
    FileName fileName,
    GroupName groupName,
    Tag tag,
    SuiteConfigurationId suiteConfigurationId,
    TestConfigurationId testConfigurationId,
    List<WorkerVariable> variables) {

  public RunWorkerCommand {
    int nonNullCount =
        Stream.of(fileName, groupName, suiteConfigurationId, testConfigurationId)
            .mapToInt(field -> field != null ? 1 : 0)
            .sum();

    if (nonNullCount == 0) {
      throw new IllegalArgumentException(
          "At least one identifier must be provided: fileName, groupName, suiteConfigurationId, or testConfigurationId");
    }

    if (nonNullCount > 1) {
      throw new IllegalArgumentException(
          "Only one identifier must be provided: fileName, groupName, suiteConfigurationId, or testConfigurationId");
    }
  }

  public WorkerType getWorkerType() {
    if (fileName() != null) return WorkerType.FILE;
    if (groupName() != null) return WorkerType.GROUP;
    if (suiteConfigurationId() != null) return WorkerType.SUITE;
    if (testConfigurationId() != null) return WorkerType.TEST;
    return WorkerType.ALL;
  }
}
