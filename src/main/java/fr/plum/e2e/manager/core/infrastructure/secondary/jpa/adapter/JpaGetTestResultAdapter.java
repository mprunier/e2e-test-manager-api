package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailsView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ApplicationScoped
@Transactional
public class JpaGetTestResultAdapter implements GetTestResultPort {

  @Override
  public List<TestResultView> findAll(TestConfigurationId testConfigurationId) {
    return List.of();
  }

  @Override
  public TestResultErrorDetailsView findErrorDetail(TestResultId testResultId) {
    return null;
  }

  @Override
  public byte[] findScreenshot(TestResultScreenshotId testResultScreenshotId) {
    return new byte[0];
  }

  @Override
  public byte[] findVideo(TestResultVideoId testResultVideoId) {
    return new byte[0];
  }
}
