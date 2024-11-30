package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.TestResultMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestScreenshotEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestVideoEntity;
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
    testResults.forEach(
        testResult -> {
          var testResultEntity = TestResultMapper.toEntity(testResult);
          testResultEntity.persist();
          if (testResult.getVideo() != null) {
            JpaTestVideoEntity.builder()
                .id(testResult.getVideo().getId().value())
                .testResultId(testResultEntity.getId())
                .video(testResult.getVideo().getVideo())
                .build()
                .persist();
          }
          if (testResult.getScreenshots() != null) {
            testResult
                .getScreenshots()
                .forEach(
                    screenshot ->
                        JpaTestScreenshotEntity.builder()
                            .id(screenshot.getId().value())
                            .testResultId(testResultEntity.getId())
                            .screenshot(screenshot.getScreenshot())
                            .build()
                            .persist());
          }
        });
  }

  @Override
  public void clearAllWorkerId(WorkerId id) {
    JpaTestResultEntity.update(
        "UPDATE JpaTestResultEntity set workerId = NULL WHERE workerId = ?1", id.value());
  }
}
