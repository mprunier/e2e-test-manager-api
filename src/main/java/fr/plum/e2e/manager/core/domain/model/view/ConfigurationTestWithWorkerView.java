package fr.plum.e2e.manager.core.domain.model.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ConfigurationTestWithWorkerView(
    UUID id,
    String title,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt,
    List<WorkerGroupView> workerGroups) {

  public static ConfigurationTestWithWorkerView from(
      ConfigurationTestView test, List<Worker> workers) {
    return builder()
        .id(test.id())
        .title(test.title())
        .status(test.status())
        .variables(test.variables())
        .tags(test.tags())
        .lastPlayedAt(test.lastPlayedAt())
        .workerGroups(WorkerGroupView.findForTest(test.id(), workers))
        .build();
  }

  public static List<ConfigurationTestWithWorkerView> fromList(
      List<ConfigurationTestView> tests, List<Worker> workers) {
    return tests.stream().map(test -> from(test, workers)).toList();
  }
}
