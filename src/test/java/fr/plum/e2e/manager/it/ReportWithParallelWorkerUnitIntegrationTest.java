package fr.plum.e2e.manager.it;

import static org.mockito.Mockito.when;

import fr.plum.e2e.manager.core.application.command.worker.ReportWorkerCommandHandler;
import fr.plum.e2e.manager.core.application.query.suite.SearchSuiteQueryHandler;
import fr.plum.e2e.manager.core.application.query.testresult.GetAllTestResultQueryHandler;
import fr.plum.e2e.manager.core.application.query.worker.GetTypeAllWorkerQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.port.WorkerUnitPort;
import fr.plum.e2e.manager.it.resource.PostgresTestResource;
import fr.plum.e2e.manager.it.utils.SqlTestService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
@QuarkusTestResource(PostgresTestResource.class)
class ReportWithParallelWorkerUnitIntegrationTest {

  public static final EnvironmentId ENVIRONMENT_ID =
      new EnvironmentId(UUID.fromString("a13aae1b-385a-4d3b-85b9-e0a4f62386fd"));

  @Inject SqlTestService sqlTestService;

  @Inject ReportWorkerCommandHandler reportWorkerCommandHandler;
  @Inject GetTypeAllWorkerQueryHandler getTypeAllWorkerQueryHandler;
  @Inject SearchSuiteQueryHandler searchSuiteQueryHandler;
  @Inject GetAllTestResultQueryHandler getAllTestResultQueryHandler;

  @InjectMock WorkerUnitPort workerUnitPort;

  private static final SourceCodeInformation SOURCE_CODE_INFORMATION =
      SourceCodeInformation.builder().projectId("1391").token("xxxx").branch("master").build();

  private Map<FileName, List<TestConfigurationId>> testConfigurationIdsByFileName;

  @BeforeEach
  void setUp() {
    sqlTestService.executeSqlScript("sql/environment.sql");
    sqlTestService.executeSqlScript("sql/file_configuration.sql");
    sqlTestService.executeSqlScript("sql/suite_configuration.sql");
    sqlTestService.executeSqlScript("sql/test_configuration.sql");
    sqlTestService.executeSqlScript("sql/worker_and_worker_unit.sql");

    var searchSuiteResults =
        searchSuiteQueryHandler.execute(
            SearchSuiteConfigurationQuery.builder()
                .environmentId(ENVIRONMENT_ID)
                .allNotSuccess(false)
                .page(0)
                .size(9999)
                .sortField("file")
                .sortOrder("ASC")
                .build());

    testConfigurationIdsByFileName = new HashMap<>();
    searchSuiteResults
        .getContent()
        .forEach(
            suite -> {
              suite
                  .tests()
                  .forEach(
                      test -> {
                        var fileName = new FileName(suite.file());
                        var testConfigurationId = new TestConfigurationId(test.id());
                        testConfigurationIdsByFileName
                            .computeIfAbsent(fileName, k -> new ArrayList<>())
                            .add(testConfigurationId);
                      });
            });
  }

  @AfterEach
  void tearDown() {
    sqlTestService.executeSqlScript("sql/clean.sql");
  }

  @Test
  void shouldReportSuccessfully() {
    // ---------------------------------
    // --------- Worker Unit 1 ---------
    // ---------------------------------
    // Given
    var workerUnitId1 = new WorkerUnitId("468061");
    var command1 = ReportWorkerCommand.builder().workerUnitId(workerUnitId1).build();

    mockGetWorkerStatus(workerUnitId1, WorkerUnitStatus.FAILED);
    mockGetWorkerReportArtifacts("worker-unit-1.zip", workerUnitId1);

    // When
    reportWorkerCommandHandler.execute(command1);

    // Then
    assertWorkerUnit(workerUnitId1);

    // ---------------------------------
    // --------- Worker Unit 2 ---------
    // ---------------------------------
    // Given
    var workerUnitId2 = new WorkerUnitId("468062");
    var command2 = ReportWorkerCommand.builder().workerUnitId(workerUnitId2).build();

    mockGetWorkerStatus(workerUnitId2, WorkerUnitStatus.FAILED);
    mockGetWorkerReportArtifacts("worker-unit-2.zip", workerUnitId2);

    // When
    reportWorkerCommandHandler.execute(command2);

    // Then
    assertWorkerUnit(workerUnitId2);

    // ---------------------------------
    // --------- Worker Unit 3 ---------
    // ---------------------------------
    // Given
    var workerUnitId3 = new WorkerUnitId("468063");
    var command3 = ReportWorkerCommand.builder().workerUnitId(workerUnitId3).build();

    mockGetWorkerStatus(workerUnitId3, WorkerUnitStatus.SUCCESS);
    mockGetWorkerReportArtifacts("worker-unit-3.zip", workerUnitId3);

    // When
    reportWorkerCommandHandler.execute(command3);

    // Then
    assertWorkerUnit(workerUnitId3);

    // ---------------------------------
    // --------- Worker Unit 4 ---------
    // ---------------------------------
    // Given
    var workerUnitId4 = new WorkerUnitId("468064");
    var command4 = ReportWorkerCommand.builder().workerUnitId(workerUnitId4).build();

    mockGetWorkerStatus(workerUnitId4, WorkerUnitStatus.FAILED);
    mockGetWorkerReportArtifacts("worker-unit-4.zip", workerUnitId4);

    // When
    reportWorkerCommandHandler.execute(command4);

    // Then
    assertLastWorkerUnit();
  }

  private void assertLastWorkerUnit() {
    var optionalWorker = getTypeAllWorkerQueryHandler.execute(new CommonQuery(ENVIRONMENT_ID));
    Assertions.assertFalse(optionalWorker.isPresent());

    var testConfigurationIds =
        testConfigurationIdsByFileName.values().stream().flatMap(List::stream).toList();
    testConfigurationIds.forEach(
        testConfigurationId -> {
          var results =
              getAllTestResultQueryHandler.execute(new GetAllTestResultQuery(testConfigurationId));
          Assertions.assertEquals(1, results.size());
          var result = results.getFirst();
          Assertions.assertNotEquals(TestResultStatus.NO_CORRESPONDING_TEST, result.status());
          Assertions.assertNotEquals(TestResultStatus.UNKNOWN, result.status());
          Assertions.assertNotEquals(TestResultStatus.SYSTEM_ERROR, result.status());
          Assertions.assertNotEquals(TestResultStatus.NO_REPORT_ERROR, result.status());
          Assertions.assertNotEquals(TestResultStatus.CANCELED, result.status());
        });
  }

  private void assertWorkerUnit(WorkerUnitId workerUnitId) {
    var optionalWorker = getTypeAllWorkerQueryHandler.execute(new CommonQuery(ENVIRONMENT_ID));
    Assertions.assertTrue(optionalWorker.isPresent());
    var worker = optionalWorker.get();
    Assertions.assertEquals(4, worker.getWorkerUnits().size());

    var optionalTargetWorkerUnit =
        worker.getWorkerUnits().stream().filter(wu -> wu.getId().equals(workerUnitId)).findFirst();
    Assertions.assertTrue(optionalTargetWorkerUnit.isPresent());
    var targetWorkerUnit = optionalTargetWorkerUnit.get();
    Assertions.assertTrue(targetWorkerUnit.isFinish());
    targetWorkerUnit
        .getFilter()
        .fileNames()
        .forEach(
            fileName -> {
              var testConfigurationIds = testConfigurationIdsByFileName.get(fileName);
              testConfigurationIds.forEach(
                  testConfigurationId -> {
                    var results =
                        getAllTestResultQueryHandler.execute(
                            new GetAllTestResultQuery(testConfigurationId));
                    Assertions.assertEquals(1, results.size());
                    var result = results.getFirst();
                    Assertions.assertNotEquals(
                        TestResultStatus.NO_CORRESPONDING_TEST, result.status());
                    Assertions.assertNotEquals(TestResultStatus.UNKNOWN, result.status());
                    Assertions.assertNotEquals(TestResultStatus.SYSTEM_ERROR, result.status());
                    Assertions.assertNotEquals(TestResultStatus.NO_REPORT_ERROR, result.status());
                    Assertions.assertNotEquals(TestResultStatus.CANCELED, result.status());
                  });
            });
  }

  private void mockGetWorkerReportArtifacts(String zip, WorkerUnitId workerUnitId) {
    Response mockResponse = Mockito.mock(Response.class);
    byte[] zipContent = readZipContent(zip);
    when(mockResponse.readEntity(byte[].class)).thenReturn(zipContent);
    when(workerUnitPort.getWorkerReportArtifacts(SOURCE_CODE_INFORMATION, workerUnitId))
        .thenReturn(mockResponse);
  }

  private void mockGetWorkerStatus(WorkerUnitId workerUnitId, WorkerUnitStatus status) {
    when(workerUnitPort.getWorkerStatus(SOURCE_CODE_INFORMATION, workerUnitId)).thenReturn(status);
  }

  private byte[] readZipContent(String zipFileName) {
    try {
      Path zipPath =
          Path.of("src/test/resources/cypress/gitlab/report/parallelworkerunit/" + zipFileName);
      return Files.readAllBytes(zipPath);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read zip file: " + zipFileName, e);
    }
  }
}
