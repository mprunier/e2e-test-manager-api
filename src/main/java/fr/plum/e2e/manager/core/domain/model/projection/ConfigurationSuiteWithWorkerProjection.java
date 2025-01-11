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
public record ConfigurationSuiteWithWorkerProjection(
    UUID id,
    String title,
    String file,
    ConfigurationStatus status,
    List<String> variables,
    List<String> tags,
    List<ConfigurationTestWithWorkerProjection> tests,
    ZonedDateTime lastPlayedAt,
    boolean hasNewTest,
    String group,
    List<WorkerProjection> workers) {

  public static ConfigurationSuiteWithWorkerProjection from(
      ConfigurationSuiteProjection suite, List<Worker> workers) {
    var suiteWorkers = WorkerProjection.findForSuite(suite.file(), suite.id(), workers);
    var testViews =
        ConfigurationTestWithWorkerProjection.from(suite.tests(), workers, suiteWorkers);
    var allWorkers = mergeWorkers(suiteWorkers, testViews);

    return builder()
        .id(suite.id())
        .title(suite.title())
        .file(suite.file())
        .status(suite.status())
        .variables(suite.variables())
        .tags(suite.tags())
        .tests(testViews)
        .lastPlayedAt(suite.lastPlayedAt())
        .hasNewTest(suite.hasNewTest())
        .group(suite.group())
        .workers(allWorkers)
        .build();
  }

  private static List<WorkerProjection> mergeWorkers(
      List<WorkerProjection> suiteWorkers, List<ConfigurationTestWithWorkerProjection> testViews) {

    var testWorkers =
        testViews.stream()
            .flatMap(test -> test.workers().stream())
            .filter(w -> w.type() == WorkerType.TEST)
            .toList();

    return Stream.concat(suiteWorkers.stream(), testWorkers.stream()).distinct().toList();
  }
}
