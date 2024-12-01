package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.mapper;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.END_TEST_JS_PATH;
import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.END_TEST_TS_PATH;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.CypressConstant.SCREENSHOT_EXTENSION;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.CypressConstant.SCREENSHOT_PATH;
import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.CypressConstant.START_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.plum.e2e.manager.core.domain.model.aggregate.report.Report;
import fr.plum.e2e.manager.core.domain.model.aggregate.report.ReportSuite;
import fr.plum.e2e.manager.core.domain.model.aggregate.report.ReportTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultScreenshot;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultDuration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultScreenshotTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultUrlError;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.vo.TestResultVideoId;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportResultInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportSuiteInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportTestInternal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkerReportMapper {

  private static final int MAX_FILENAME_LENGTH = 255;
  private static final String DELIMITER = " -- ";
  private static final String ERROR_SCREENSHOT_KEY = "Failure Screenshot";

  public static Report convertToWorkerReportResult(
      MochaReportResultInternal result,
      Map<String, byte[]> screenshots,
      Map<String, byte[]> videos) {

    return Report.builder()
        .fileName(new FileName(result.getFile().replace(START_PATH, "")))
        .tests(mapTests(result.getTests(), screenshots, videos, "", result.getFile()))
        .suites(mapSuites(result.getSuites(), screenshots, videos, result.getFile()))
        .build();
  }

  private static List<ReportTest> mapTests(
      List<MochaReportTestInternal> tests,
      Map<String, byte[]> screenshots,
      Map<String, byte[]> videos,
      String suitePath,
      String specFile) {
    return tests.stream()
        .map(
            test -> convertToWorkerReportResultTest(test, screenshots, videos, suitePath, specFile))
        .toList();
  }

  private static List<ReportSuite> mapSuites(
      List<MochaReportSuiteInternal> suites,
      Map<String, byte[]> screenshots,
      Map<String, byte[]> videos,
      String specFile) {
    return suites.stream()
        .map(suite -> convertToWorkerReportResultSuite(suite, screenshots, videos, specFile))
        .toList();
  }

  private static ReportSuite convertToWorkerReportResultSuite(
      MochaReportSuiteInternal suite,
      Map<String, byte[]> screenshots,
      Map<String, byte[]> videos,
      String specFile) {
    var suitePath = suite.getTitle() + DELIMITER;

    return ReportSuite.builder()
        .title(new SuiteTitle(suite.getTitle()))
        .tests(mapTests(suite.getTests(), screenshots, videos, suitePath, specFile))
        .build();
  }

  private static ReportTest convertToWorkerReportResultTest(
      MochaReportTestInternal test,
      Map<String, byte[]> screenshots,
      Map<String, byte[]> videos,
      String suitePath,
      String specFile) {
    var context = parseTestContext(test);

    return ReportTest.builder()
        .title(new TestTitle(test.getTitle()))
        .duration(new TestResultDuration(test.getDuration()))
        .status(test.status())
        .code(new TestResultCode(test.getCode()))
        .errorMessage(new TestResultErrorMessage(test.getErr().getMessage()))
        .errorStackTrace(new TestResultErrorStackTrace(test.getErr().getEstack()))
        .reference(new TestResultReference(context.reference()))
        .urlError(new TestResultUrlError(context.urlError()))
        .screenshots(mapScreenshots(context, screenshots, suitePath, test.getTitle(), specFile))
        .video(mapVideo(videos, specFile))
        .build();
  }

  private static List<TestResultScreenshot> mapScreenshots(
      TestContext context,
      Map<String, byte[]> screenshots,
      String suitePath,
      String testTitle,
      String specFile) {
    var testScreenshots = new ArrayList<TestResultScreenshot>();

    if (context.screenshotError() != null) {
      screenshots.forEach(
          (name, data) -> {
            if (removeScreenshotPrefixAndExtension(context.screenshotError())
                .contains(removeScreenshotPrefixAndExtension(name))) {
              testScreenshots.add(
                  TestResultScreenshot.builder()
                      .id(TestResultScreenshotId.generate())
                      .title(new TestResultScreenshotTitle(ERROR_SCREENSHOT_KEY))
                      .screenshot(data)
                      .build());
            }
          });
    } else {
      var testPath = generateTestPath(suitePath, testTitle, specFile);
      screenshots.forEach(
          (name, data) -> {
            if (name.contains(testPath)) {
              var screenshotName =
                  name.substring(name.lastIndexOf(DELIMITER) + DELIMITER.length()).trim();
              testScreenshots.add(
                  TestResultScreenshot.builder()
                      .id(TestResultScreenshotId.generate())
                      .title(new TestResultScreenshotTitle(screenshotName))
                      .screenshot(data)
                      .build());
            }
          });
    }

    return testScreenshots;
  }

  private static TestResultVideo mapVideo(Map<String, byte[]> videos, String specFile) {
    var videoKey = removeVideoPrefixAndExtension(specFile);

    var video =
        videos.entrySet().stream()
            .filter(
                entry ->
                    removeVideoPrefixAndExtension(entry.getKey()).contains(videoKey)
                        || videoKey.contains(removeVideoPrefixAndExtension(entry.getKey())))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse(null);

    if (video != null) {
      return TestResultVideo.builder().id(TestResultVideoId.generate()).video(video).build();
    }
    return null;
  }

  private record TestContext(String reference, String urlError, String screenshotError) {}

  private static TestContext parseTestContext(MochaReportTestInternal test) {
    String reference = null;
    String urlError = null;
    String screenshotError = null;

    try {
      var contextList = test.getContextParse();
      if (contextList != null) {
        for (var context : contextList) {
          switch (context.getTitle()) {
            case "reference" -> reference = context.getValue();
            case "urlError" -> urlError = context.getValue();
            case "screenshotError" -> screenshotError = context.getValue();
          }
        }
      }
    } catch (JsonProcessingException e) {
      log.error("Error parsing testFilter context", e);
    }

    return new TestContext(reference, urlError, screenshotError);
  }

  private static String generateTestPath(String suitePath, String testTitle, String specFile) {
    var basePath = removeTestFileExtensions(specFile) + DELIMITER + suitePath + testTitle;

    if (basePath.length() <= MAX_FILENAME_LENGTH) {
      return basePath;
    }

    var specPart = removeTestFileExtensions(specFile) + DELIMITER;
    var availableLength = MAX_FILENAME_LENGTH - specPart.length();
    var suiteAndTestPart = suitePath + testTitle;

    return specPart
        + (suiteAndTestPart.length() > availableLength
            ? suiteAndTestPart.substring(0, availableLength)
            : suiteAndTestPart);
  }

  private static String removeTestFileExtensions(String filename) {
    return filename.replace(END_TEST_JS_PATH, "").replace(END_TEST_TS_PATH, "");
  }

  private static String removeScreenshotPrefixAndExtension(String screenshotName) {
    return screenshotName.replace(SCREENSHOT_PATH, "").replace(SCREENSHOT_EXTENSION, "");
  }

  private static String removeVideoPrefixAndExtension(String screenshotName) {
    return screenshotName.replace(START_PATH, "");
  }
}
