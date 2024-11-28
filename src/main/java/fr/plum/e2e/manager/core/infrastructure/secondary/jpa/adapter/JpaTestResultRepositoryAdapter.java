package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.TestResultMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaTestResultRepositoryAdapter implements TestResultRepositoryPort {

  @Override
  public void saveAll(List<TestResult> testResults) {
    TestResultMapper.toEntities(testResults).forEach(entity -> entity.persist());
  }

  @Override
  public void clearAllWorkerId(WorkerId id) {
    JpaTestResultEntity.update("workerId = NULL WHERE workerId = ?1", id.value());
  }
}
