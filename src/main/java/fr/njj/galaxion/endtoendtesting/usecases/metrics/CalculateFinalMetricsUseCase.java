package fr.njj.galaxion.endtoendtesting.usecases.metrics;

import fr.njj.galaxion.endtoendtesting.domain.record.Metrics;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CalculateFinalMetricsUseCase {

    private final EnvironmentRetrievalService environmentRetrievalService;

    @Transactional
    public Metrics execute(
            long environmentId) {

        var environment = environmentRetrievalService.get(environmentId);
        var configurationSuites = environment.getConfigurationSuites();

        var metrics = Metrics.builder().at(ZonedDateTime.now()).build();
        for (ConfigurationSuiteEntity configurationSuite : configurationSuites) {
            metrics = calculateMetrics(configurationSuite, metrics);
        }

        var passPercent = metrics.passes() * 100 / (metrics.tests() != 0 ? metrics.tests() : 1);

        return Metrics
                .builder()
                .at(metrics.at())
                .suites(metrics.suites())
                .tests(metrics.tests())
                .passPercent(passPercent)
                .passes(metrics.passes())
                .failures(metrics.failures())
                .skipped(metrics.skipped())
                .build();
    }

    public Metrics calculateMetrics(ConfigurationSuiteEntity suite, Metrics metrics) {
        int suites = metrics.suites() + 1;
        int tests = metrics.tests();
        int passes = metrics.passes();
        int failures = metrics.failures();
        int skipped = metrics.skipped();

        var configurationTests = suite.getConfigurationTests();
        if (configurationTests != null) {
            tests += configurationTests.size();
            for (var configurationTest : configurationTests) {
                switch (configurationTest.getStatus()) {
                    case SUCCESS:
                        passes++;
                        break;
                    case FAILED, SYSTEM_ERROR, NO_CORRESPONDING_TEST, NO_REPORT_ERROR, UNKNOWN:
                        failures++;
                        break;
                    default:
                        skipped++;
                        break;
                }
            }
        }

        if (suite.getSubSuites() != null) {
            for (var subSuite : suite.getSubSuites()) {
                var subMetrics = calculateMetrics(subSuite, metrics);
                tests += subMetrics.tests();
                passes += subMetrics.passes();
                failures += subMetrics.failures();
                skipped += subMetrics.skipped();
                suites += subMetrics.suites();
            }
        }

        return Metrics
                .builder()
                .at(ZonedDateTime.now())
                .suites(suites)
                .tests(tests)
                .passes(passes)
                .failures(failures)
                .skipped(skipped)
                .build();
    }

}

