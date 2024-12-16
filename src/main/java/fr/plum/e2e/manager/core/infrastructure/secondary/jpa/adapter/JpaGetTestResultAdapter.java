package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailsView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultScreenshotView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultVariableView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testresult.JpaTestResultEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaGetTestResultAdapter implements GetTestResultPort {

  private final EntityManager entityManager;

  @Override
  public List<TestResultView> findAll(TestConfigurationId testConfigurationId) {
    var query =
        entityManager.createQuery(
            """
                SELECT DISTINCT tr,
                       s.id as screenshot_id,
                       s.filename as screenshot_name,
                       v.id as video_id
                FROM JpaTestResultEntity tr
                LEFT JOIN JpaTestScreenshotEntity s ON s.testResultId = tr.id
                LEFT JOIN JpaTestVideoEntity v ON v.testResultId = tr.id
                WHERE tr.configurationTestId = :testConfigurationId
                ORDER BY tr.createdAt DESC
                """,
            Object[].class);
    query.setParameter("testConfigurationId", testConfigurationId.value());

    Map<UUID, List<TestResultScreenshotView>> screenshotsPerTest = new HashMap<>();
    Map<UUID, UUID> videoPerTest = new HashMap<>();
    Map<UUID, JpaTestResultEntity> entities = new HashMap<>();

    for (Object[] row : query.getResultList()) {
      JpaTestResultEntity entity = (JpaTestResultEntity) row[0];
      UUID screenshotId = (UUID) row[1];
      String screenshotName = (String) row[2];
      UUID videoId = (UUID) row[3];

      entities.putIfAbsent(entity.getId(), entity);

      if (videoId != null) {
        videoPerTest.putIfAbsent(entity.getId(), videoId);
      }

      if (screenshotId != null && screenshotName != null) {
        screenshotsPerTest
            .computeIfAbsent(entity.getId(), k -> new ArrayList<>())
            .add(new TestResultScreenshotView(screenshotId, screenshotName));
      }
    }

    return entities.values().stream()
        .map(
            entity ->
                new TestResultView(
                    entity.getId(),
                    entity.getStatus(),
                    entity.getReference(),
                    entity.getCreatedAt(),
                    entity.getErrorUrl(),
                    entity.getDuration(),
                    entity.getCreatedBy(),
                    screenshotsPerTest.getOrDefault(entity.getId(), List.of()),
                    videoPerTest.get(entity.getId()),
                    entity.getVariables() != null
                        ? entity.getVariables().entrySet().stream()
                            .map(
                                entry ->
                                    new TestResultVariableView(entry.getKey(), entry.getValue()))
                            .toList()
                        : List.of()))
        .toList();
  }

  @Override
  public TestResultErrorDetailsView findErrorDetail(TestResultId testResultId) {
    var query =
        entityManager.createQuery(
            """
      SELECT NEW fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailsView(
          tr.errorMessage,
          tr.errorStacktrace,
          tr.code
      )
      FROM JpaTestResultEntity tr
      WHERE tr.id = :testResultId
      """,
            TestResultErrorDetailsView.class);
    query.setParameter("testResultId", testResultId.value());

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public byte[] findScreenshot(TestResultScreenshotId testResultScreenshotId) {
    var query =
        entityManager.createQuery(
            """
        SELECT s.screenshot FROM JpaTestScreenshotEntity s
        WHERE s.id = :screenshotId
        """,
            byte[].class);
    query.setParameter("screenshotId", testResultScreenshotId.value());

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public byte[] findVideo(TestResultVideoId testResultVideoId) {
    var query =
        entityManager.createQuery(
            """
        SELECT v.video FROM JpaTestVideoEntity v
        WHERE v.id = :videoId
        """,
            byte[].class);
    query.setParameter("videoId", testResultVideoId.value());

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }
}
