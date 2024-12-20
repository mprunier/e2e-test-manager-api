package fr.plum.e2e.manager.core.domain.model.projection;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Builder;

@Builder
public record ConfigurationTestWithWorkerProjection(
    UUID id,
    String title,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    ZonedDateTime lastPlayedAt,
    List<WorkerProjection> workers) {

  public static List<ConfigurationTestWithWorkerProjection> from(
      List<ConfigurationTestProjection> tests,
      List<Worker> workers,
      List<WorkerProjection> suiteWorkers) {

    return tests.stream()
        .map(
            test -> {
              List<WorkerProjection> testWorkers =
                  Stream.concat(
                          WorkerProjection.findForTest(test.id(), workers).stream(),
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

  public static ConfigurationTestWithWorkerProjection from(
      ConfigurationTestProjection test, List<Worker> workers) {
    return builder()
        .id(test.id())
        .title(test.title())
        .status(test.status())
        .variables(test.variables())
        .tags(test.tags())
        .lastPlayedAt(test.lastPlayedAt())
        .workers(WorkerProjection.findForTest(test.id(), workers))
        .build();
  }
}
