package fr.plum.e2e.manager.core.domain.port.view;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.projection.TestResultErrorDetailsProjection;
import fr.plum.e2e.manager.core.domain.model.projection.TestResultProjection;
import java.util.List;

public interface GetTestResultPort {

  List<TestResultProjection> findAll(TestConfigurationId testConfigurationId);

  TestResultErrorDetailsProjection findErrorDetail(TestResultId testResultId);

  byte[] findScreenshot(TestResultScreenshotId testResultScreenshotId);

  byte[] findVideo(TestResultVideoId testResultVideoId);
}
