package fr.njj.galaxion.endtoendtesting.helper;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.exception.TestInProgressException;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class TestHelper {

    private final TestRetrievalService testRetrievalService;

    public void assertNotInProgressTestByEnvironmentId(Long id) {
        var testNumber = testRetrievalService.countInProgressTestEntityByEnvironmentId(id);
        if (testNumber > 0) {
            throw new TestInProgressException();
        }
    }

    public static void updateStatus(List<TestEntity> tests, ConfigurationStatus status, boolean withSuite) {
        tests.forEach(test -> updateStatus(test, status, withSuite));
    }

    public static void updateStatus(TestEntity test, ConfigurationStatus status, boolean withSuite) {
        test.setStatus(status);

//        var configurationTest = test.getConfigurationTest();
//        configurationTest.setStatus(status);
//
//        if (withSuite) {
//            var configurationSuite = configurationTest.getConfigurationSuite();
//            if (ConfigurationStatus.NO_CORRESPONDING_TEST.equals(status)) {
//                configurationSuite.setStatus(ConfigurationStatus.FAILED);
//            } else if (ConfigurationStatus.SUCCESS.equals(status) || ConfigurationStatus.SKIPPED.equals(status)) {
//                var hasOneFailed = checkOneFailed(configurationSuite, configurationTest);
//                if (hasOneFailed.get()) {
//                    configurationSuite.setStatus(ConfigurationStatus.FAILED);
//                } else {
//                    var hasOneInProgress = checkOneInProgress(configurationSuite, configurationTest);
//                    if (!hasOneInProgress.get()) {
//                        configurationSuite.setStatus(ConfigurationStatus.SUCCESS);
//                    }
//                }
//            } else {
//                configurationSuite.setStatus(ConfigurationStatus.FAILED);
//            }
//        }
    }

    public static AtomicBoolean checkOneFailed(ConfigurationSuiteEntity configurationSuite, ConfigurationTestEntity configurationTest) {
        var suiteTests = configurationSuite.getConfigurationTests();
        AtomicBoolean hasOneFailed = new AtomicBoolean(false);
        suiteTests.forEach(suiteTest -> {
            if (!suiteTest.getId().equals(configurationTest.getId()) &&
                (suiteTest.getStatus().equals(ConfigurationStatus.NO_CORRESPONDING_TEST) ||
                 suiteTest.getStatus().equals(ConfigurationStatus.FAILED) ||
                 suiteTest.getStatus().equals(ConfigurationStatus.SYSTEM_ERROR) ||
                 suiteTest.getStatus().equals(ConfigurationStatus.NO_REPORT_ERROR) ||
                 suiteTest.getStatus().equals(ConfigurationStatus.UNKNOWN))
            ) {
                hasOneFailed.set(true);
            }
        });
        return hasOneFailed;
    }

    public static AtomicBoolean checkOneInProgress(ConfigurationSuiteEntity configurationSuite, ConfigurationTestEntity configurationTest) {
        var suiteTests = configurationSuite.getConfigurationTests();
        AtomicBoolean hasOneFailed = new AtomicBoolean(false);
        suiteTests.forEach(suiteTest -> {
            if (!suiteTest.getId().equals(configurationTest.getId()) &&
                (suiteTest.getStatus().equals(ConfigurationStatus.IN_PROGRESS))
            ) {
                hasOneFailed.set(true);
            }
        });
        return hasOneFailed;
    }
}

