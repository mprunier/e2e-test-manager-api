package fr.plum.e2e.manager.core.domain.model.aggregate.testresult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultDuration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultUrlError;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVariable;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TestResultTest {

  private TestResultId testResultId;
  private WorkerId workerId;
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
  private AuditInfo auditInfo;

  @BeforeEach
  void setUp() {
    testResultId = TestResultId.generate();
    workerId = new WorkerId(UUID.randomUUID());
    testConfigurationId = new TestConfigurationId(UUID.randomUUID());
    status = TestResultStatus.SUCCESS;
    reference = new TestResultReference("REF-001");
    errorUrl = new TestResultUrlError("http://error.url");
    errorMessage = new TestResultErrorMessage("Error occurred");
    errorStacktrace = new TestResultErrorStackTrace("Stack trace");
    code = new TestResultCode("CODE-001");
    duration = new TestResultDuration(1000);
    variables = new ArrayList<>();
    variables.add(new TestResultVariable("key", "value"));
    screenshots = new ArrayList<>();
    screenshots.add(createScreenshot());
    video = createVideo();
    auditInfo = AuditInfo.create(new ActionUsername("testUser"), ZonedDateTime.now());
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_test_result() {
      // WHEN
      TestResult result =
          TestResult.builder()
              .testResultId(testResultId)
              .auditInfo(auditInfo)
              .workerId(workerId)
              .testConfigurationId(testConfigurationId)
              .status(status)
              .reference(reference)
              .errorUrl(errorUrl)
              .errorMessage(errorMessage)
              .errorStacktrace(errorStacktrace)
              .code(code)
              .duration(duration)
              .variables(variables)
              .screenshots(screenshots)
              .video(video)
              .build();

      // THEN
      assertThat(result.getId()).isEqualTo(testResultId);
      assertThat(result.getWorkerId()).isEqualTo(workerId);
      assertThat(result.getTestConfigurationId()).isEqualTo(testConfigurationId);
      assertThat(result.getStatus()).isEqualTo(status);
      assertThat(result.getReference()).isEqualTo(reference);
      assertThat(result.getErrorUrl()).isEqualTo(errorUrl);
      assertThat(result.getErrorMessage()).isEqualTo(errorMessage);
      assertThat(result.getErrorStacktrace()).isEqualTo(errorStacktrace);
      assertThat(result.getCode()).isEqualTo(code);
      assertThat(result.getDuration()).isEqualTo(duration);
      assertThat(result.getVariables()).isEqualTo(variables);
      assertThat(result.getScreenshots()).isEqualTo(screenshots);
      assertThat(result.getVideo()).isEqualTo(video);
      assertThat(result.getAuditInfo()).isEqualTo(auditInfo);
    }

    @Test
    void should_throw_exception_when_workerId_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestResult.builder()
                      .testResultId(testResultId)
                      .auditInfo(auditInfo)
                      .workerId(null)
                      .testConfigurationId(testConfigurationId)
                      .status(status)
                      .variables(variables)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field workerId is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_testConfigurationId_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestResult.builder()
                      .testResultId(testResultId)
                      .auditInfo(auditInfo)
                      .workerId(workerId)
                      .testConfigurationId(null)
                      .status(status)
                      .variables(variables)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description",
              "The field testConfigurationId is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_status_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestResult.builder()
                      .testResultId(testResultId)
                      .auditInfo(auditInfo)
                      .workerId(workerId)
                      .testConfigurationId(testConfigurationId)
                      .status(null)
                      .variables(variables)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field status is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_variables_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestResult.builder()
                      .testResultId(testResultId)
                      .auditInfo(auditInfo)
                      .workerId(workerId)
                      .testConfigurationId(testConfigurationId)
                      .status(status)
                      .variables(null)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field variables is mandatory and cannot be empty or null.");
    }
  }

  private TestResultScreenshot createScreenshot() {
    return TestResultScreenshot.create(
        new TestResultScreenshotTitle("Shot 1"), "screenshot-data".getBytes());
  }

  private TestResultVideo createVideo() {
    return TestResultVideo.create("video-data".getBytes());
  }
}
