// package fr.plum.e2e.manager.core.domain.usecase.worker;
//
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.eq;
// import static org.mockito.Mockito.doAnswer;
// import static org.mockito.Mockito.never;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.when;
//
// import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
// import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
// import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
// import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
// import
// fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeBranch;
// import
// fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeProjectId;
// import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeToken;
// import fr.plum.e2e.manager.core.domain.model.aggregate.report.Report;
// import fr.plum.e2e.manager.core.domain.model.aggregate.report.ReportTest;
// import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
// import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
// import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
// import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
// import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
// import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResult;
// import fr.plum.e2e.manager.core.domain.model.aggregate.testresult.TestResultStatus;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
// import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
// import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
// import fr.plum.e2e.manager.core.domain.model.event.WorkerCompletedEvent;
// import fr.plum.e2e.manager.core.domain.model.event.WorkerUnitCompletedEvent;
// import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
// import fr.plum.e2e.manager.core.domain.port.out.WorkerExtractorPort;
// import fr.plum.e2e.manager.core.domain.port.out.WorkerUnitPort;
// import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
// import fr.plum.e2e.manager.core.domain.port.out.repository.TestConfigurationRepositoryPort;
// import fr.plum.e2e.manager.core.domain.port.out.repository.TestResultRepositoryPort;
// import fr.plum.e2e.manager.core.domain.port.out.repository.WorkerRepositoryPort;
// import fr.plum.e2e.manager.sharedkernel.domain.port.out.ClockPort;
// import fr.plum.e2e.manager.sharedkernel.domain.port.out.TransactionManagerPort;
// import java.time.ZonedDateTime;
// import java.util.ArrayList;
// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class ReportWorkerUseCaseTest {
//
//  @Mock private ClockPort clockPort;
//  @Mock private EventPublisherPort eventPublisher;
//  @Mock private WorkerUnitPort workerUnitPort;
//  @Mock private TestConfigurationRepositoryPort testConfigurationRepository;
//  @Mock private WorkerRepositoryPort workerRepository;
//  @Mock private WorkerExtractorPort workerExtractor;
//  @Mock private TestResultRepositoryPort testResultRepository;
//  @Mock private TransactionManagerPort transactionManager;
//  @Mock private EnvironmentRepositoryPort environmentRepository;
//
//  private ReportWorkerUseCase useCase;
//  private final ZonedDateTime NOW = ZonedDateTime.now();
//  private final EnvironmentId ENV_ID = EnvironmentId.generate();
//  private final ActionUsername USERNAME = new ActionUsername("testUser");
//  private final WorkerUnitId WORKER_UNIT_ID = new WorkerUnitId("worker-1");
//
//  @BeforeEach
//  void setUp() {
//    useCase =
//        new ReportWorkerUseCase(
//            clockPort,
//            eventPublisher,
//            workerUnitPort,
//            testConfigurationRepository,
//            workerRepository,
//            workerExtractor,
//            testResultRepository,
//            transactionManager,
//            environmentRepository);
//  }
//
//  @Test
//  void execute_WorkerNotFound_DoNothing() {
//    // Given
//    ReportWorkerCommand command = new ReportWorkerCommand(WORKER_UNIT_ID);
//    when(workerRepository.find(WORKER_UNIT_ID)).thenReturn(Optional.empty());
//
//    // When
//    useCase.execute(command);
//
//    // Then
//    verify(workerUnitPort, never()).getWorkerStatus(any(), any());
//    verify(testResultRepository, never()).saveAll(any());
//  }
//
//  @Test
//  void execute_WorkerInProgress_DoNothing() {
//    // Given
//    ReportWorkerCommand command = new ReportWorkerCommand(WORKER_UNIT_ID);
//    Worker worker = createTestWorker(WorkerType.FILE);
//    Environment environment = createTestEnvironment();
//
//    when(workerRepository.find(WORKER_UNIT_ID)).thenReturn(Optional.of(worker));
//    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
//    when(workerUnitPort.getWorkerStatus(any(), eq(WORKER_UNIT_ID)))
//        .thenReturn(WorkerUnitStatus.IN_PROGRESS);
//
//    // When
//    useCase.execute(command);
//
//    // Then
//    verify(testResultRepository, never()).saveAll(any());
//    verify(workerRepository, never()).save(any());
//  }
//
//  @Test
//  void execute_WorkerSuccess_ProcessResults() {
//    // Given
//    ReportWorkerCommand command = new ReportWorkerCommand(WORKER_UNIT_ID);
//    Worker worker = createTestWorker(WorkerType.FILE);
//    Environment environment = createTestEnvironment();
//    Report report = createTestReport();
//    TestConfigurationId testConfigId = TestConfigurationId.generate();
//
//    when(workerRepository.find(WORKER_UNIT_ID)).thenReturn(Optional.of(worker));
//    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
//    when(workerUnitPort.getWorkerStatus(any(), eq(WORKER_UNIT_ID)))
//        .thenReturn(WorkerUnitStatus.SUCCESS);
//    when(workerUnitPort.getWorkerReportArtifacts(any(), eq(WORKER_UNIT_ID)))
//        .thenReturn("artifacts");
//    when(workerExtractor.extractWorkerReportArtifacts(any()))
//        .thenReturn(Collections.singletonList(report));
//    when(testConfigurationRepository.findId(any(), any(), any()))
//        .thenReturn(Optional.of(testConfigId));
//    when(clockPort.now()).thenReturn(NOW);
//
//    // When
//    useCase.execute(command);
//
//    // Then
//    verify(testResultRepository).saveAll(any());
//    verify(eventPublisher).publishAsync(any(WorkerUnitCompletedEvent.class));
//
//    ArgumentCaptor<List<TestResult>> resultsCaptor = ArgumentCaptor.forClass(List.class);
//    verify(testResultRepository).saveAll(resultsCaptor.capture());
//    List<TestResult> savedResults = resultsCaptor.getValue();
//    assert !savedResults.isEmpty();
//  }
//
//  @Test
//  void execute_WorkerCanceled_CreateCanceledResults() {
//    // Given
//    ReportWorkerCommand command = new ReportWorkerCommand(WORKER_UNIT_ID);
//    Worker worker = createTestWorker(WorkerType.TEST);
//    Environment environment = createTestEnvironment();
//    TestConfigurationId testConfigId = TestConfigurationId.generate();
//
//    when(workerRepository.find(WORKER_UNIT_ID)).thenReturn(Optional.of(worker));
//    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
//    when(workerUnitPort.getWorkerStatus(any(), eq(WORKER_UNIT_ID)))
//        .thenReturn(WorkerUnitStatus.CANCELED);
//    when(testConfigurationRepository.findAllIds(any(), any(List.class)))
//        .thenReturn(Collections.singletonList(testConfigId));
//    when(clockPort.now()).thenReturn(NOW);
//
//    // When
//    useCase.execute(command);
//
//    // Then
//    ArgumentCaptor<List<TestResult>> resultsCaptor = ArgumentCaptor.forClass(List.class);
//    verify(testResultRepository).saveAll(resultsCaptor.capture());
//    List<TestResult> savedResults = resultsCaptor.getValue();
//    assert !savedResults.isEmpty();
//    assert savedResults.getFirst().getStatus() == TestResultStatus.CANCELED;
//  }
//
//  @Test
//  void execute_WorkerCompleted_FinalizeWorker() {
//    // Given
//    ReportWorkerCommand command = new ReportWorkerCommand(WORKER_UNIT_ID);
//    Worker worker = createTestWorker(WorkerType.FILE);
//    Environment environment = createTestEnvironment();
//
//    when(workerRepository.find(WORKER_UNIT_ID)).thenReturn(Optional.of(worker));
//    when(environmentRepository.find(ENV_ID)).thenReturn(Optional.of(environment));
//    when(workerUnitPort.getWorkerStatus(any(), eq(WORKER_UNIT_ID)))
//        .thenReturn(WorkerUnitStatus.SUCCESS);
//    when(clockPort.now()).thenReturn(NOW);
//    doAnswer(
//            invocation -> {
//              Runnable runnable = invocation.getArgument(0);
//              runnable.run();
//              return null;
//            })
//        .when(transactionManager)
//        .registerAfterCommit(any(Runnable.class));
//
//    // When
//    useCase.execute(command);
//
//    // Then
//    verify(testResultRepository).updateParentsConfigurationStatus(worker.getId());
//    verify(testResultRepository).clearAllWorkerId(worker.getId());
//    verify(workerRepository).delete(worker.getId());
//    verify(eventPublisher).publishAsync(any(WorkerCompletedEvent.class));
//  }
//
//  private Worker createTestWorker(WorkerType type) {
//    Worker worker = Worker.initialize(ENV_ID, type);
//    worker.createAuditInfo(USERNAME, NOW);
//
//    WorkerUnitFilter filter = new WorkerUnitFilter(new ArrayList<>(), null, null, null);
//
//    WorkerUnit unit = WorkerUnit.builder().id(WORKER_UNIT_ID).filter(filter).build();
//
//    worker.addWorkerUnit(unit);
//    return worker;
//  }
//
//  private Environment createTestEnvironment() {
//    SourceCodeInformation sourceCodeInfo =
//        new SourceCodeInformation(
//            new SourceCodeProjectId("test-project"),
//            new SourceCodeToken("test-token"),
//            new SourceCodeBranch("main"));
//
//    return Environment.builder()
//        .id(ENV_ID)
//        .environmentDescription(new EnvironmentDescription("Test Environment"))
//        .sourceCodeInformation(sourceCodeInfo)
//        .auditInfo(AuditInfo.create(USERNAME, NOW))
//        .build();
//  }
//
//  private Report createTestReport() {
//    ReportTest test = ReportTest.builder().title(new TestTitle("Test 1")).build();
//
//    return Report.builder()
//        .fileName(new FileName("test.spec.js"))
//        .tests(Collections.singletonList(test))
//        .build();
//  }
// }
