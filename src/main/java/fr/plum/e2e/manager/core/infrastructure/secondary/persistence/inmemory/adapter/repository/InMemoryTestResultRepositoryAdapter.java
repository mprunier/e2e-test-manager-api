package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.port.repository.TestResultRepositoryPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTestResultRepositoryAdapter implements TestResultRepositoryPort {
  private static final Map<String, TestResult> testResults = new HashMap<>();

  @Override
  public void saveAll(List<TestResult> results) {
    results.forEach(result -> testResults.put(result.getId().value().toString(), result));
  }

  @Override
  public void clearAllWorkerId(WorkerId id) {
    testResults.values().removeIf(result -> result.getWorkerId().equals(id));
  }

  @Override
  public void updateParentsConfigurationStatus(WorkerId workerId) {
    // Done by infrastructure
  }

  public static Map<String, TestResult> findAll() {
    return testResults;
  }
}
