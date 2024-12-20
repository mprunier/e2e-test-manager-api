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
    List<WorkerView> workers) {

  public static ConfigurationSuiteWithWorkerView from(
      ConfigurationSuiteView suite, List<Worker> workers) {
    var suiteWorkers = WorkerView.findForSuite(suite.file(), suite.id(), workers);
    var testViews = ConfigurationTestWithWorkerView.from(suite.tests(), workers, suiteWorkers);
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

  private static List<WorkerView> mergeWorkers(
      List<WorkerView> suiteWorkers, List<ConfigurationTestWithWorkerView> testViews) {

    var testWorkers =
        testViews.stream()
            .flatMap(test -> test.workers().stream())
            .filter(w -> w.type() == WorkerType.TEST)
            .toList();

    return Stream.concat(suiteWorkers.stream(), testWorkers.stream()).distinct().toList();
  }
}
