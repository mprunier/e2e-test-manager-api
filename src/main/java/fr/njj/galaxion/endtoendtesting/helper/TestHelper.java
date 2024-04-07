package fr.njj.galaxion.endtoendtesting.helper;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.exception.TestInProgressException;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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

    public static void updateStatus(List<TestEntity> tests, ConfigurationStatus status) {
        tests.forEach(test -> updateStatus(test, status));
    }

    public static void updateStatus(TestEntity test, ConfigurationStatus status) {
        test.setStatus(status);
    }
}

