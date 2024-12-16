package fr.plum.e2e.manager.core.domain.model.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record ConfigurationTestWithWorkerView(
    UUID id,
    String title,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt,
    List<WorkerView> workers) {

  public static List<ConfigurationTestWithWorkerView> from(
      List<ConfigurationTestView> tests, List<Worker> workers, List<WorkerView> suiteWorkers) {

    return tests.stream()
        .map(
            test -> {
              List<WorkerView> testWorkers =
                  Stream.concat(
                          WorkerView.findForTest(test.id(), workers).stream(),
                          suiteWorkers.stream().filter(w -> w.type() == WorkerType.SUITE))
                      .distinct()
                      .toList();

              return builder()
                  .id(test.id())
                  .title(test.title())
                  .status(test.status())
                  .variables(test.variables())
                  .tags(test.tags())
                  .lastPlayedAt(test.lastPlayedAt())
                  .workers(testWorkers)
                  .build();
            })
        .toList();
  }

  public static ConfigurationTestWithWorkerView from(
      ConfigurationTestView test, List<Worker> workers) {
    return builder()
        .id(test.id())
        .title(test.title())
        .status(test.status())
        .variables(test.variables())
        .tags(test.tags())
        .lastPlayedAt(test.lastPlayedAt())
        .workers(WorkerView.findForTest(test.id(), workers))
        .build();
  }
}
