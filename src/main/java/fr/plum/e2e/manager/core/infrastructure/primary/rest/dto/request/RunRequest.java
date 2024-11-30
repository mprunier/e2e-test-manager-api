package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public record RunRequest(
    String fileName,
    UUID configurationSuiteId,
    UUID configurationTestId,
    String tag,
    String groupName,
    List<RunVariableRequest> variables) {

  public RunWorkerCommand toCommand(UUID environmentId, String username) {
    return new RunWorkerCommand(
        environmentId != null ? new EnvironmentId(environmentId) : null,
        username != null ? new ActionUsername(username) : null,
        StringUtils.isNotBlank(fileName) ? new FileName(fileName) : null,
        StringUtils.isNotBlank(groupName) ? new GroupName(groupName) : null,
        StringUtils.isNotBlank(tag) ? new Tag(tag) : null,
        configurationSuiteId != null ? new SuiteConfigurationId(configurationSuiteId) : null,
        configurationTestId != null ? new TestConfigurationId(configurationTestId) : null,
        variables != null
            ? variables.stream().map(RunVariableRequest::toCommand).toList()
            : new ArrayList<>());
  }
}
