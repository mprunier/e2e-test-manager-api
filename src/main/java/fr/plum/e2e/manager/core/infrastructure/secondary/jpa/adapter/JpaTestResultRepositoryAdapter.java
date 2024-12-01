package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper.TestResultMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestScreenshotEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestVideoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaTestResultRepositoryAdapter implements TestResultRepositoryPort {

  @Inject EntityManager em;

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

  @Override
  public void updateParentsConfigurationStatus(WorkerId workerId) {
    em.createNativeQuery(
            """
            WITH latest_results AS (
                SELECT DISTINCT ON (tr.configuration_test_id)
                    tr.configuration_test_id,
                    tr.status
                FROM test_result tr
                WHERE tr.worker_id = :workerId
                ORDER BY tr.configuration_test_id, tr.created_at DESC
            )
            UPDATE test_configuration tc
            SET
                status = CASE
                    WHEN lr.status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN') THEN 'FAILED'
                    ELSE lr.status::text
                END,
                last_played_at = NOW()
            FROM latest_results lr
            WHERE tc.id = lr.configuration_test_id
            """)
        .setParameter("workerId", workerId)
        .executeUpdate();

    // Update suite configurations
    em.createNativeQuery(
            """
            WITH affected_tests AS (
                SELECT DISTINCT tc.suite_id, tc.status
                FROM test_configuration tc
                WHERE tc.id IN (
                    SELECT DISTINCT tr.configuration_test_id
                    FROM test_result tr
                    WHERE tr.worker_id = :workerId
                )
            )
            UPDATE suite_configuration sc
            SET status = (
                SELECT
                    CASE
                        WHEN bool_or(at.status = 'FAILED') THEN 'FAILED'
                        WHEN bool_or(at.status = 'CANCELED') THEN 'CANCELED'
                        WHEN bool_or(at.status = 'NEW') THEN 'NEW'
                        WHEN bool_or(at.status = 'SUCCESS') AND bool_or(at.status = 'SKIPPED') THEN 'PARTIAL_SKIPPED'
                        WHEN bool_and(at.status = 'SKIPPED') THEN 'SKIPPED'
                        ELSE 'SUCCESS'
                    END
                FROM affected_tests at
                WHERE at.suite_id = sc.id
                GROUP BY at.suite_id
            ),
            last_played_at = NOW()
            WHERE id IN (
                SELECT DISTINCT suite_id FROM affected_tests
            )
            """)
        .setParameter("workerId", workerId)
        .executeUpdate();
  }
}
