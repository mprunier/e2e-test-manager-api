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
import java.util.List;
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
        SELECT tr FROM JpaTestResultEntity tr
        WHERE tr.configurationTestId = :testConfigurationId
        ORDER BY tr.createdAt DESC
        """,
            JpaTestResultEntity.class);
    query.setParameter("testConfigurationId", testConfigurationId.value());

    return query.getResultList().stream()
        .map(
            entity -> {
              var screenshotsQuery =
                  entityManager.createQuery(
                      """
              SELECT NEW fr.plum.e2e.manager.core.domain.model.view.TestResultScreenshotView(
                  s.id,
                  s.filename
              )
              FROM JpaTestScreenshotEntity s
              WHERE s.testResultId = :testResultId
              """,
                      TestResultScreenshotView.class);
              screenshotsQuery.setParameter("testResultId", entity.getId());

              var videoQuery =
                  entityManager.createQuery(
                      """
                      SELECT v.id FROM JpaTestVideoEntity v
                      WHERE v.testResultId = :testResultId
                      """,
                      UUID.class);
              videoQuery.setParameter("testResultId", entity.getId());
              videoQuery.setMaxResults(1);

              UUID videoId;
              try {
                videoId = videoQuery.setMaxResults(1).getSingleResult();
              } catch (NoResultException e) {
                videoId = null;
              }

              return TestResultView.builder()
                  .id(entity.getId())
                  .status(entity.getStatus())
                  .reference(entity.getReference())
                  .createdAt(entity.getCreatedAt())
                  .errorUrl(entity.getErrorUrl())
                  .duration(entity.getDuration())
                  .createdBy(entity.getCreatedBy())
                  .screenshots(screenshotsQuery.getResultList())
                  .videoId(videoId)
                  .variables(
                      entity.getVariables() != null
                          ? entity.getVariables().entrySet().stream()
                              .map(
                                  entry ->
                                      new TestResultVariableView(entry.getKey(), entry.getValue()))
                              .toList()
                          : List.of())
                  .build();
            })
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
