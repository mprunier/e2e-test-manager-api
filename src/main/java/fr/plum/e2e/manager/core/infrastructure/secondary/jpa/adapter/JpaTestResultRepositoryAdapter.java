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
                            .filename(screenshot.getTitle().value())
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
            status = (
                SELECT
                    CASE
                        WHEN bool_or(status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN')) THEN 'FAILED'
                        WHEN bool_or(status = 'CANCELED') THEN 'CANCELED'
                        WHEN bool_or(status = 'SKIPPED') THEN 'SKIPPED'
                        ELSE 'SUCCESS'
                    END
                FROM latest_results lr
                WHERE lr.configuration_test_id = tc.id
            ),
            last_played_at = NOW()
        WHERE tc.id IN (SELECT configuration_test_id FROM latest_results)
        """)
        .setParameter("workerId", workerId.value())
        .executeUpdate();

    em.createNativeQuery(
            """
        WITH test_counts AS (
            SELECT
                tc.suite_id,
                count(*) FILTER (WHERE tc.status = 'SUCCESS') as succeeded,
                count(*) FILTER (WHERE tc.status = 'FAILED') as failed,
                count(*) FILTER (WHERE tc.status = 'CANCELED') as canceled,
                count(*) FILTER (WHERE tc.status = 'SKIPPED') as skipped
            FROM test_configuration tc
            WHERE tc.id IN (
                SELECT configuration_test_id
                FROM test_result
                WHERE worker_id = :workerId
            )
            GROUP BY tc.suite_id
        )
        UPDATE suite_configuration sc
        SET
            status = CASE
                WHEN failed > 0 THEN 'FAILED'
                WHEN canceled > 0 THEN 'CANCELED'
                WHEN succeeded > 0 AND skipped > 0 THEN 'PARTIAL_SKIPPED'
                WHEN succeeded = 0 AND skipped > 0 THEN 'SKIPPED'
                ELSE 'SUCCESS'
            END,
            last_played_at = NOW()
        FROM test_counts
        WHERE sc.id = test_counts.suite_id
        """)
        .setParameter("workerId", workerId.value())
        .executeUpdate();
  }
}
