package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestIdentifierEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSuiteRepository;
import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationTestRepository;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final ConfigurationSuiteRepository configurationSuiteRepository;
    private final ConfigurationTestRepository configurationTestRepository;
    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

    @Transactional
    public void deleteConfigurationByFile(String file,
                                          long environmentId) {
        log.info("Environment id [{}] : Remove file [{}].", environmentId, file);
        configurationTestRepository.deleteByFileAndEnv(file, environmentId);
        configurationSuiteRepository.deleteByFileAndEnv(file, environmentId);
    }

    @Transactional
    public void updateOrCreate(Long environmentId,
                               String file,
                               ConfigurationInternal configurationInternal) {
        var environment = environmentRetrievalService.getEnvironment(environmentId);

        var suiteIds = new ArrayList<Long>();
        var testIds = new ArrayList<Long>();

        if (!configurationInternal.getTests().isEmpty()) {
            configurationInternal.getTests()
                                 .forEach(testInternal -> updateOrCreateTestWithoutSuite(environment, file, testInternal, suiteIds, testIds));
        }
        if (!configurationInternal.getSuites().isEmpty()) {
            configurationInternal.getSuites()
                                 .forEach(suiteInternal -> updateOrCreateSuite(environment, file, suiteInternal, null, suiteIds, testIds));
        }

        configurationTestRepository.deleteByEnvAndFileAndNotInTestIds(environmentId, file, testIds);
        configurationSuiteRepository.deleteByEnvAndFileAndNotInSuiteIds(environmentId, file, suiteIds);
    }

    private void updateOrCreateTestWithoutSuite(EnvironmentEntity environment,
                                                String file,
                                                ConfigurationTestInternal testInternal,
                                                List<Long> suiteIds,
                                                List<Long> testIds) {
        ConfigurationSuiteEntity configurationSuite;
        var configurationSuiteOptional = configurationSuiteRetrievalService.getBy(environment.getId(), file, NO_SUITE, null);
        if (configurationSuiteOptional.isEmpty()) {
            configurationSuite = ConfigurationSuiteEntity
                    .builder()
                    .environment(environment)
                    .title(NO_SUITE)
                    .file(file)
                    .build();
            configurationSuite.persist();
        } else {
            configurationSuite = configurationSuiteOptional.get();
        }
        suiteIds.add(configurationSuite.getId());

        updateOrCreateTest(environment, file, testInternal, configurationSuite, testIds);
    }

    private void updateOrCreateSuite(EnvironmentEntity environment,
                                     String file,
                                     ConfigurationSuiteInternal suiteInternal,
                                     ConfigurationSuiteEntity parentSuite,
                                     List<Long> suiteIds,
                                     List<Long> testIds) {
        ConfigurationSuiteEntity configurationSuite;
        var parentSuiteId = parentSuite != null ? parentSuite.getId() : null;
        var configurationSuiteOptional = configurationSuiteRetrievalService.getBy(environment.getId(), file, suiteInternal.getTitle(), parentSuiteId);
        if (configurationSuiteOptional.isEmpty()) {
            configurationSuite = ConfigurationSuiteEntity
                    .builder()
                    .environment(environment)
                    .title(suiteInternal.getTitle())
                    .file(file)
                    .parentSuite(parentSuite)
                    .variables(CollectionUtils.isEmpty(suiteInternal.getVariables()) ? null : suiteInternal.getVariables())
                    .build();
            configurationSuite.persist();
        } else {
            configurationSuite = configurationSuiteOptional.get();
            configurationSuite.setUpdatedAt(ZonedDateTime.now());
            configurationSuite.setVariables(CollectionUtils.isEmpty(suiteInternal.getVariables()) ? null : suiteInternal.getVariables());
        }
        suiteIds.add(configurationSuite.getId());

        suiteInternal.getTests().forEach(testInternal -> updateOrCreateTest(environment, file, testInternal, configurationSuite, testIds));
        suiteInternal.getSuites().forEach(suiteInternalChild -> updateOrCreateSuite(environment, file, suiteInternalChild, configurationSuite, suiteIds, testIds));
    }

    private void updateOrCreateTest(EnvironmentEntity environment,
                                    String file,
                                    ConfigurationTestInternal testInternal,
                                    ConfigurationSuiteEntity configurationSuite,
                                    List<Long> testIds) {
        ConfigurationTestEntity configurationTest;
        var configurationTestOptional = configurationTestRetrievalService.getBy(environment.getId(), file, testInternal.getTitle(), configurationSuite);

        var configurationTestIdentifierEntities = new ArrayList<ConfigurationTestIdentifierEntity>();
        if (configurationTestOptional.isEmpty()) {
            configurationTest = ConfigurationTestEntity
                    .builder()
                    .environment(environment)
                    .configurationSuite(configurationSuite)
                    .title(testInternal.getTitle())
                    .file(file)
                    .variables(CollectionUtils.isEmpty(testInternal.getVariables()) ? null : testInternal.getVariables())
                    .build();
            configurationTest.persist();
            testInternal.getIdentifiers().forEach(identifier -> {
                var configurationTestIdentifierEntity = ConfigurationTestIdentifierEntity.builder().configurationTest(configurationTest).identifier(identifier).environmentId(configurationTest.getEnvironment().getId()).build();
                configurationTestIdentifierEntities.add(configurationTestIdentifierEntity);
                configurationTestIdentifierEntity.persist();
            });
            configurationTest.setConfigurationIdentifiers(configurationTestIdentifierEntities);
        } else {
            configurationTest = configurationTestOptional.get();
            configurationTest.getConfigurationIdentifiers().clear();
            testInternal.getIdentifiers().forEach(identifier -> {
                var configurationTestIdentifierEntity = ConfigurationTestIdentifierEntity.builder().configurationTest(configurationTest).identifier(identifier).environmentId(configurationTest.getEnvironment().getId()).build();
                configurationTestIdentifierEntities.add(configurationTestIdentifierEntity);
                configurationTestIdentifierEntity.persist();
            });
            configurationTest.setVariables(CollectionUtils.isEmpty(testInternal.getVariables()) ? null : testInternal.getVariables());
            configurationTest.setUpdatedAt(ZonedDateTime.now());
        }
        testIds.add(configurationTest.getId());
    }

}

