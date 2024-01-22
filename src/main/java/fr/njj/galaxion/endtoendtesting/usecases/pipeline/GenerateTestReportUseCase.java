package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.SCREENSHOT_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestScreenshotEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GenerateTestReportUseCase {

  @Transactional
  public void execute(ArtifactDataInternal artifactData, List<TestEntity> tests) {
    var screenshots = artifactData.getScreenshots();
    var videos = artifactData.getVideos();
    var report = artifactData.getReport();
    if (report != null && report.getResults() != null && !report.getResults().isEmpty()) {
      tests.forEach(
          test -> {
            AtomicBoolean isNotFound = new AtomicBoolean(true);
            var configurationTest = test.getConfigurationTest();
            var file = configurationTest.getFile();
            var title = configurationTest.getTitle();
            var fullTitle = new StringBuilder();
            var configurationSuite = configurationTest.getConfigurationSuite();

            fullTitle.append(title);
            buildSuiteTitles(fullTitle, configurationSuite);

            report
                .getResults()
                .forEach(
                    result -> {
                      var reportFile = result.getFile().replaceAll(START_PATH, "");

                      if (file.equals(reportFile)) {

                        Optional<MochaReportTestInternal> mochaTestOptional;

                        if (fullTitle.toString().equals(configurationTest.getTitle())) {
                          mochaTestOptional =
                              result.getTests().stream()
                                  .filter(
                                      mochaTest ->
                                          fullTitle.toString().equals(mochaTest.getTitle()))
                                  .findFirst();

                        } else {
                          mochaTestOptional =
                              findTestInSuites(result.getSuites(), fullTitle.toString());
                        }
                        if (mochaTestOptional.isPresent()) {
                          isNotFound.set(false);
                          updateTest(test, mochaTestOptional.get(), screenshots);
                          if (tests.size() == 1 && !videos.isEmpty()) {
                            test.setVideo(videos.values().stream().findFirst().get());
                          }
                        }
                      }
                    });
            if (isNotFound.get()) {
              updateStatus(test, ConfigurationStatus.NO_CORRESPONDING_TEST);
            }
          });
    } else {
      updateStatus(tests, ConfigurationStatus.NO_REPORT_ERROR);
    }
  }

  private Optional<MochaReportTestInternal> findTestInSuites(
      List<MochaReportSuiteInternal> mochaSuites, String fullTitle) {
    for (var mochaSuite : mochaSuites) {
      for (var mochaTest : mochaSuite.getTests()) {
        if (fullTitle.equals(mochaTest.getFullTitle())) {
          return Optional.of(mochaTest);
        }
      }
      if (mochaSuite.getSuites() != null && !mochaSuite.getSuites().isEmpty()) {
        var foundTest = findTestInSuites(mochaSuite.getSuites(), fullTitle);
        if (foundTest.isPresent()) {
          return foundTest;
        }
      }
    }
    return Optional.empty();
  }

  private void updateTest(
      TestEntity test, MochaReportTestInternal mochaTest, Map<String, byte[]> screenshots) {
    var status = getStatus(mochaTest);
    updateStatus(test, status);
    test.setErrorMessage(mochaTest.getErr() != null ? mochaTest.getErr().getMessage() : null);
    test.setErrorStacktrace(mochaTest.getErr() != null ? mochaTest.getErr().getEstack() : null);
    test.setCode(mochaTest.getCode());
    test.setDuration(mochaTest.getDuration());
    try {
      var contextList = mochaTest.getContextParse();
      if (contextList != null) {
        contextList.stream()
            .filter(item -> "reference".equals(item.getTitle()))
            .findFirst()
            .ifPresent(
                mochaReportContextInternal ->
                    test.setReference(mochaReportContextInternal.getValue()));
        contextList.stream()
            .filter(item -> "urlError".equals(item.getTitle()))
            .findFirst()
            .ifPresent(
                mochaReportContextInternal ->
                    test.setErrorUrl(mochaReportContextInternal.getValue()));
      }
    } catch (JsonProcessingException e) {
      test.setReference("No Reference");
    }

    createScreenshot(test, screenshots);
  }

  private ConfigurationStatus getStatus(MochaReportTestInternal mochaTest) {
    if (Boolean.TRUE.equals(mochaTest.getPass())) {
      return ConfigurationStatus.SUCCESS;
    }
    if (Boolean.TRUE.equals(mochaTest.getPending())
        || Boolean.TRUE.equals(mochaTest.getSkipped())) {
      return ConfigurationStatus.SKIPPED;
    }
    return ConfigurationStatus.FAILED;
  }

  private void createScreenshot(TestEntity test, Map<String, byte[]> screenshots) {
    var testTitleWithSuiteTitles = new ArrayList<String>();
    var testTitle = test.getConfigurationTest().getTitle();
    testTitle = testTitle.replace(":", ""); // TODO add other character
    testTitleWithSuiteTitles.add(testTitle);
    buildTestTitleWithSuiteTitles(
        testTitleWithSuiteTitles, test.getConfigurationTest().getConfigurationSuite());
    testTitleWithSuiteTitles.remove(NO_SUITE);

    for (var entry : screenshots.entrySet()) {
      boolean isTestScreenshot;
      if (testTitleWithSuiteTitles.size() != 1) {
        isTestScreenshot =
            testTitleWithSuiteTitles.stream()
                .allMatch(suiteTitle -> entry.getKey().contains(suiteTitle));
      } else {
        isTestScreenshot =
            entry.getKey().contains(test.getConfigurationTest().getTitle())
                && !entry.getKey().contains(" -- ");
      }
      if (isTestScreenshot) {
        TestScreenshotEntity.builder()
            .test(test)
            .filename(entry.getKey().replaceAll(SCREENSHOT_PATH, ""))
            .screenshot(entry.getValue())
            .build()
            .persist();
      }
    }
  }

  private void buildSuiteTitles(
      StringBuilder fullTitle, ConfigurationSuiteEntity configurationSuite) {
    if (!configurationSuite.getTitle().equals(NO_SUITE)) {
      fullTitle.insert(0, configurationSuite.getTitle() + " ");
      if (configurationSuite.getParentSuite() != null) {
        buildSuiteTitles(fullTitle, configurationSuite.getParentSuite());
      }
    }
  }

  private void buildTestTitleWithSuiteTitles(
      ArrayList<String> suiteTitles, ConfigurationSuiteEntity configurationSuite) {
    var suiteTitle = configurationSuite.getTitle();
    suiteTitle = suiteTitle.replace(":", ""); // TODO add other character
    suiteTitles.add(suiteTitle);
    if (configurationSuite.getParentSuite() != null) {
      buildTestTitleWithSuiteTitles(suiteTitles, configurationSuite.getParentSuite());
    }
  }
}
