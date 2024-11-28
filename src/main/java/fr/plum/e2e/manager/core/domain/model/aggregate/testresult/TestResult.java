package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import fr.plum.e2e.manager.core.domain.model.aggregate.report.ReportTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultDuration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultUrlError;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class TestResult extends AggregateRoot<TestResultId> {

  private WorkerId workerId; // When not null, TestResult is hidden. Only during worker execution.

  private TestConfigurationId testConfigurationId;

  private TestResultStatus status;

  private TestResultReference reference;
  private TestResultUrlError errorUrl;
  private TestResultErrorMessage errorMessage;
  private TestResultErrorStackTrace errorStacktrace;
  private TestResultCode code;
  private TestResultDuration duration;
  private List<TestResultVariable> variables;

  private List<TestResultScreenshot> screenshots;
  private TestResultVideo video;

  public static TestResult create(
      Worker worker, TestConfigurationId testConfigurationId, ReportTest testWithoutSuite) {
    return builder()
        .workerId(worker.getId())
        .testConfigurationId(testConfigurationId)
        .status(testWithoutSuite.status())
        .reference(testWithoutSuite.reference())
        .errorUrl(testWithoutSuite.urlError())
        .errorMessage(testWithoutSuite.errorMessage())
        .errorStacktrace(testWithoutSuite.errorStackTrace())
        .code(testWithoutSuite.code())
        .duration(testWithoutSuite.duration())
        .variables(buildVariables(worker))
        .screenshots(testWithoutSuite.screenshots())
        .video(testWithoutSuite.video())
        .build();
  }

  public static TestResult createWithoutInformation(
      Worker worker, TestConfigurationId testConfigurationId, TestResultStatus status) {
    return builder()
        .workerId(worker.getId())
        .testConfigurationId(testConfigurationId)
        .status(status)
        .variables(buildVariables(worker))
        .build();
  }

  private static List<TestResultVariable> buildVariables(Worker worker) {
    return worker.getVariables().stream()
        .map(variable -> new TestResultVariable(variable.name(), variable.value()))
        .toList();
  }
}
