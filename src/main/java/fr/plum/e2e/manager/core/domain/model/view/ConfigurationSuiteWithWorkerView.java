package fr.plum.e2e.manager.core.domain.model.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfigurationSuiteWithWorkerView(
    UUID id,
    String title,
    String file,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    List<ConfigurationTestWithWorkerView> tests,
    ZonedDateTime lastPlayedAt,
    boolean hasNewTest,
    String group,
    List<WorkerGroupView> workerGroups) {

  public static ConfigurationSuiteWithWorkerView from(
      ConfigurationSuiteView suite, List<Worker> workers) {
    return builder()
        .id(suite.id())
        .title(suite.title())
        .file(suite.file())
        .status(suite.status())
        .variables(suite.variables())
        .tags(suite.tags())
        .tests(ConfigurationTestWithWorkerView.fromList(suite.tests(), workers))
        .lastPlayedAt(suite.lastPlayedAt())
        .hasNewTest(suite.hasNewTest())
        .group(suite.group())
        .workerGroups(WorkerGroupView.findForSuite(suite.file(), suite.id(), workers))
        .build();
  }
}
