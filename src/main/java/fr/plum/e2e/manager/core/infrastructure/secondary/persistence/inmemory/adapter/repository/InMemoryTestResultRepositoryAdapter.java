package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.port.repository.TestResultRepositoryPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryTestResultRepositoryAdapter implements TestResultRepositoryPort {
  private final List<TestResult> results = new ArrayList<>();

  @Override
  public void saveAll(List<TestResult> testResults) {
    results.addAll(testResults);
  }

  @Override
  public void clearAllWorkerId(WorkerId id) {
    // No-op for tests
  }

  @Override
  public void updateParentsConfigurationStatus(WorkerId workerId) {
    // No-op for tests
  }

  public List<TestResult> getResults() {
    return Collections.unmodifiableList(results);
  }
}
