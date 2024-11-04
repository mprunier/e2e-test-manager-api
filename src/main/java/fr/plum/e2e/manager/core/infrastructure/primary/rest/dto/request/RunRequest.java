package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import java.util.List;
import java.util.UUID;

public record RunRequest(
    String fileName,
    UUID configurationSuiteId,
    UUID configurationTestId,
    String tag,
    String groupName,
    List<RunVariableRequest> variables) {

  public RunWorkerCommand toCommand(UUID environmentId, String username) {
    return new RunWorkerCommand(
        new EnvironmentId(environmentId),
        new ActionUsername(username),
        new FileName(fileName),
        new GroupName(groupName),
        new Tag(tag),
        new SuiteConfigurationId(configurationSuiteId),
        new TestConfigurationId(configurationTestId),
        variables.stream().map(RunVariableRequest::toCommand).toList());
  }
}
