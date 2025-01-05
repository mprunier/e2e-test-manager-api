package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.plum.e2e.manager.core.domain.model.aggregate.report.Report;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.CypressWorkerExtractorAdapter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class CypressWorkerExtractorAdapterTest {

  private final CypressWorkerExtractorAdapter cypressWorkerExtractorAdapter =
      new CypressWorkerExtractorAdapter();

  @Test
  void withScreenshotNameInContext_shouldExtractReportsFromZipFile() throws IOException {
    // Given
    String zipFilePath = "cypress/zip/artifacts_one_failed_test_screenshot_name_in_context.zip";
    byte[] zipData = loadArtifacts(zipFilePath);
    Response mockResponse = Response.ok(zipData).build();

    // When
    List<Report> reports = cypressWorkerExtractorAdapter.extractWorkerReportArtifacts(mockResponse);

    // Then
    assertNotNull(reports);
    assertEquals(1, reports.size());

    Report report = reports.getFirst();
    assertEquals(
        "1010_sale_telesales_postpay_acquisition_mobile_cancel_flow.cy.js",
        report.fileName().value());

    // Verify suites
    assertEquals(1, report.suites().size());
    var suite = report.suites().getFirst();
    assertEquals(
        "Postpay Acquisition (Telesales) - Mobile - Cancel Flow : Other ID Card - Offer Composition without extra - Manual recurring - No upfront",
        suite.title().value());

    // Verify tests in suite
    assertEquals(2, suite.tests().size());

    // First test (failed)
    var failedTest = suite.tests().getFirst();
    assertEquals(
        "Acquisition : Add Mobile Offer - Other ID Card - Offer Composition without extra - Manual recurring - No upfront",
        failedTest.title().value());
    assertEquals(TestResultStatus.FAILED, failedTest.status());
    assertEquals(5856, failedTest.duration().value());
    assertEquals("58MX3W40", failedTest.reference().value());
    assertEquals(
        "https://malta-b2c-crm-ui.dev.epicmt.internal/acquisition/postpay/58MX3W40/offer-composition/27118",
        failedTest.urlError().value());
    assertTrue(
        failedTest.errorMessage().value().contains("CypressError: `cy.task('executeSQL')` failed"));

    // Verify screenshot for failed test
    assertEquals(1, failedTest.screenshots().size());
    var screenshot = failedTest.screenshots().getFirst();
    assertEquals("Failure Screenshot", screenshot.getTitle().value());
    assertNotNull(screenshot.getScreenshot());

    // Verify video
    assertNotNull(failedTest.video());
    assertNotNull(failedTest.video().getVideo());

    // Second test (pending)
    var pendingTest = suite.tests().get(1);
    assertEquals("Search Order : Cancel Order", pendingTest.title().value());
    assertEquals(TestResultStatus.SKIPPED, pendingTest.status());
    assertEquals(0, pendingTest.duration().value());
  }

  @Test
  void inSubDirectory_shouldExtractReportsFromZipFile() throws IOException {
    // Given
    String zipFilePath = "cypress/zip/artifacts_one_failed_test_in_sub_directory.zip";
    byte[] zipData = loadArtifacts(zipFilePath);
    Response mockResponse = Response.ok(zipData).build();

    // When
    List<Report> reports = cypressWorkerExtractorAdapter.extractWorkerReportArtifacts(mockResponse);

    // Then
    assertNotNull(reports);
    assertEquals(1, reports.size());

    Report report = reports.getFirst();
    assertEquals("add_tv/2.0.12_b2c_cross_sell_add_tv_on_dp_ftth.cy.ts", report.fileName().value());

    // Verify suites
    assertEquals(1, report.suites().size());
    var suite = report.suites().getFirst();
    assertEquals("2.0.12 - B2C Cross Sell - Add Tv On dualplay FTTH", suite.title().value());

    // Verify tests count
    assertEquals(18, suite.tests().size());

    // Verify failed test
    var failedTest =
        suite.tests().stream()
            .filter(test -> test.status() == TestResultStatus.FAILED)
            .findFirst()
            .orElseThrow();

    assertEquals("Delivery confirm", failedTest.title().value());
    assertEquals(336, failedTest.duration().value());
    assertEquals("about:blank", failedTest.urlError().value());
    assertTrue(failedTest.errorMessage().value().contains("CypressError: `cy.request()` failed"));

    // Verify screenshot for failed test
    assertEquals(1, failedTest.screenshots().size());
    var screenshot = failedTest.screenshots().getFirst();
    assertEquals("Failure Screenshot", screenshot.getTitle().value());
    assertNotNull(screenshot.getScreenshot());

    // Verify video
    assertNotNull(failedTest.video());
    assertNotNull(failedTest.video().getVideo());

    // Verify pending tests
    long pendingCount =
        suite.tests().stream().filter(test -> test.status() == TestResultStatus.SKIPPED).count();
    assertEquals(17, pendingCount);
  }

  private byte[] loadArtifacts(String zipFilePath) throws IOException {
    byte[] zipData;
    try (InputStream is =
        Objects.requireNonNull(
            getClass().getClassLoader().getResourceAsStream(zipFilePath),
            "Resource not found: " + zipFilePath)) {
      zipData = is.readAllBytes();
    }
    return zipData;
  }
}
