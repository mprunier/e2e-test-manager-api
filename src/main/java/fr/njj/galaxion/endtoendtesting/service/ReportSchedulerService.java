package fr.njj.galaxion.endtoendtesting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportResultInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportStatsInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.MochaReportTestInternal;
import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.AddMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.scheduler.UpdateSchedulerStatusUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ReportSchedulerService {

    private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;
    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final TestScreenshotService testScreenshotService;
    private final AddMetricsUseCase addMetricsUseCase;
    private final UpdateSchedulerStatusUseCase updateSchedulerStatusUseCase;

    @Transactional
    public void report(ArtifactDataInternal artifactData,
                       long environmentId) {
        var screenshots = artifactData.getScreenshots();
        var report = artifactData.getReport();
        if (report != null && report.getResults() != null && !report.getResults().isEmpty()) {
            var results = report.getResults();
            var stats = report.getStats();
            setStats(environmentId, stats);
            createSuitesAndTests(environmentId, results, screenshots);
        } else {
            updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.NO_REPORT_ERROR);
        }
    }

    private void createSuitesAndTests(long environmentId,
                                      List<MochaReportResultInternal> results,
                                      Map<String, byte[]> screenshots) {
        results.forEach(result -> {
            var file = result.getFile().replaceAll(START_PATH, "");
            processTestsWithoutSuite(environmentId, file, result.getTests(), screenshots);
            processSuites(environmentId, file, result.getSuites(), null, screenshots);
        });
    }

    private void processTestsWithoutSuite(long environmentId,
                                          String file,
                                          List<MochaReportTestInternal> tests,
                                          Map<String, byte[]> screenshots) {
        if (tests != null) {
            tests.forEach(mochaTest -> {
                var configurationSuiteOptional = configurationSuiteRetrievalService.getBy(environmentId, file, NO_SUITE, null);
                if (configurationSuiteOptional.isPresent()) {
                    var configurationTestOptional = configurationTestRetrievalService.getBy(environmentId, file, mochaTest.getTitle(), configurationSuiteOptional.get());
                    configurationTestOptional.ifPresent(configurationTestEntity -> saveTest(mochaTest, configurationTestEntity, screenshots));
                }
            });
        }
    }

    private void processTests(long environmentId,
                              String file,
                              List<MochaReportTestInternal> tests,
                              ConfigurationSuiteEntity suite,
                              Map<String, byte[]> screenshots) {
        if (tests != null) {
            tests.forEach(mochaTest -> {
                var configurationTestOptional = configurationTestRetrievalService.getBy(environmentId, file, mochaTest.getTitle(), suite);
                configurationTestOptional.ifPresent(configurationTestEntity -> saveTest(mochaTest, configurationTestEntity, screenshots));
            });
        }
    }

    private void processSuites(long environmentId,
                               String file,
                               List<MochaReportSuiteInternal> suites,
                               ConfigurationSuiteEntity parentSuite,
                               Map<String, byte[]> screenshots) {
        if (suites != null) {
            suites.forEach(mochaSuite -> {
                var configurationSuiteOptional = configurationSuiteRetrievalService.getBy(environmentId, file, mochaSuite.getTitle(), parentSuite != null ? parentSuite.getId() : null);
                if (configurationSuiteOptional.isPresent()) {
                    processTests(environmentId, file, mochaSuite.getTests(), configurationSuiteOptional.get(), screenshots);
                    processSuites(environmentId, file, mochaSuite.getSuites(), configurationSuiteOptional.get(), screenshots);
                }
            });
        }
    }

    private void saveTest(MochaReportTestInternal mochaTest,
                          ConfigurationTestEntity configurationTest,
                          Map<String, byte[]> screenshots) {

        var status = getConfigurationStatus(mochaTest);
        var test = TestEntity
                .builder()
                .configurationTest(configurationTest)
                .status(status)
                .errorMessage(mochaTest.getErr() != null ? mochaTest.getErr().getMessage() : null)
                .errorStacktrace(mochaTest.getErr() != null ? mochaTest.getErr().getEstack() : null)
                .code(mochaTest.getCode())
                .duration(mochaTest.getDuration())
                .createdBy("Scheduler")
                .build();
        try {
            var contextList = mochaTest.getContextParse();
            if (contextList != null) {
                contextList.stream()
                           .filter(item -> "reference".equals(item.getTitle()))
                           .findFirst()
                           .ifPresent(mochaReportContextInternal -> test.setReference(mochaReportContextInternal.getValue()));
                contextList.stream()
                           .filter(item -> "urlError".equals(item.getTitle()))
                           .findFirst()
                           .ifPresent(mochaReportContextInternal -> test.setErrorUrl(mochaReportContextInternal.getValue()));
            }
        } catch (JsonProcessingException e) {
            test.setReference("No Reference");
        }
        test.persist();

        testScreenshotService.create(mochaTest, screenshots, test);
    }

    private static ConfigurationStatus getConfigurationStatus(MochaReportTestInternal mochaTest) {
        if (Boolean.TRUE.equals(mochaTest.getPass())) {
            return ConfigurationStatus.SUCCESS;
        } else if (Boolean.TRUE.equals(mochaTest.getPending()) || Boolean.TRUE.equals(mochaTest.getSkipped())) {
            return ConfigurationStatus.SKIPPED;
        }
        return ConfigurationStatus.FAILED;
    }

    private void setStats(long environmentId, MochaReportStatsInternal stats) {
        if (stats != null) {
            var skipped = 0;
            if (stats.getPending() != null) {
                skipped += stats.getPending();
            }
            if (stats.getSkipped() != null) {
                skipped += stats.getSkipped();
            }

            var metric = Metrics
                    .builder()
                    .suites(stats.getSuites())
                    .tests(stats.getTests())
                    .passes(stats.getPasses())
                    .failures(stats.getFailures())
                    .skipped(skipped)
                    .passPercent(stats.getPassPercent())
                    .build();
            addMetricsUseCase.execute(environmentId, metric);
        }
    }
}
