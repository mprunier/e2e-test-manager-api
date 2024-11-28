package fr.plum.e2e.manager.core.domain.port.out.query;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailView;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import java.util.List;

public interface GetTestResultPort {

  List<TestResultView> findAll(TestConfigurationId testConfigurationId);

  TestResultErrorDetailView findErrorDetail(TestResultId testResultId);

  byte[] findScreenshot(TestResultScreenshotId testResultScreenshotId);

  byte[] findVideo(TestResultVideoId testResultVideoId);
}
