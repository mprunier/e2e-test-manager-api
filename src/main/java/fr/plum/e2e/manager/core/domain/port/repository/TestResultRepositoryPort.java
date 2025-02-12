package fr.plum.e2e.manager.core.domain.port.repository;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import java.util.List;

public interface TestResultRepositoryPort {

  void saveAll(List<TestResult> testResults);

  void clearAllWorkerId(WorkerId id);

  void updateParentsConfigurationStatus(WorkerId workerId);
}
