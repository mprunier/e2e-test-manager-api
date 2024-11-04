package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report.ReportResult;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report.ReportResultSuite;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report.ReportResultTest;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestCode;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorMessage;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorScreenshotName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorStackTrace;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestErrorUrl;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsFailed;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsSkipped;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestIsSuccess;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.ResultTestReference;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.SuiteDuration;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.result.TestDuration;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportResultInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportSuiteInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.MochaReportTestInternal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorkerReportMapper {

  public static ReportResult convertToWorkerReportResult(MochaReportResultInternal result) {
    var tests =
        result.getTests().stream()
            .map(WorkerReportMapper::convertToWorkerReportResultTest)
            .toList();
    var suites =
        result.getSuites().stream()
            .map(WorkerReportMapper::convertToWorkerReportResultSuite)
            .toList();
    return ReportResult.builder()
        .fileName(new FileName(result.getFile()))
        .tests(tests)
        .suites(suites)
        .build();
  }

  private static ReportResultSuite convertToWorkerReportResultSuite(
      MochaReportSuiteInternal suite) {
    var tests =
        suite.getTests().stream().map(WorkerReportMapper::convertToWorkerReportResultTest).toList();
    var suites =
        suite.getSuites().stream()
            .map(WorkerReportMapper::convertToWorkerReportResultSuite)
            .toList();
    return ReportResultSuite.builder()
        .title(new SuiteTitle(suite.getTitle()))
        .duration(new SuiteDuration(suite.getDuration()))
        .tests(tests)
        .suites(suites)
        .build();
  }

  private static ReportResultTest convertToWorkerReportResultTest(MochaReportTestInternal test) {
    var context = parseTestContext(test);

    return ReportResultTest.builder()
        .title(new TestTitle(test.getTitle()))
        .duration(new TestDuration(test.getDuration()))
        .isSuccess(new ResultTestIsSuccess(test.getPass()))
        .isFailed(new ResultTestIsFailed(test.getFail()))
        .isSkipped(new ResultTestIsSkipped(test.getSkipped() || test.getPending()))
        .code(new ResultTestCode(test.getCode()))
        .errorMessage(new ResultTestErrorMessage(test.getErr().getMessage()))
        .errorStacktrace(new ResultTestErrorStackTrace(test.getErr().getEstack()))
        .reference(new ResultTestReference(context.reference()))
        .urlError(new ResultTestErrorUrl(context.urlError()))
        .screenshotError(new ResultTestErrorScreenshotName(context.screenshotError()))
        .build();
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
            case "screenshotError" -> screenshotError = context.getValue();
            case "urlError" -> urlError = context.getValue();
          }
        }
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    return new TestContext(reference, urlError, screenshotError);
  }
}
