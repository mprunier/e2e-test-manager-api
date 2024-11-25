package fr.plum.e2e.manager.core.domain.port.out.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import java.util.List;

public interface TestResultRepositoryPort {

  void saveAll(List<TestResult> testResults);
}
