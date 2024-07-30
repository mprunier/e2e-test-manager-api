package fr.njj.galaxion.endtoendtesting.helper;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestHelper {

    public static void updateStatus(List<TestEntity> tests, ConfigurationStatus status) {
        tests.forEach(test -> updateStatus(test, status));
    }

    public static void updateStatus(TestEntity test, ConfigurationStatus status) {
        test.setStatus(status);
    }
}

