package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.client.tstojsconverter.TsToJsConverterClient;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.SynchronizationStatus;
import fr.njj.galaxion.endtoendtesting.domain.exception.CharactersForbiddenException;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.EnvironmentAlreadyInSyncProgressException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SuiteNoTitleException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleDuplicationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleEmptyException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationTestInternal;
import fr.njj.galaxion.endtoendtesting.helper.TestHelper;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSynchronizationEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_JS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_TS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.GitlabHelper.getChangedFilesAfterDate;
import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationInternalMapper.build;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class ConfigurationSynchronizationService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final GitlabService gitlabService;
    private final ConfigurationService configurationService;
    private final TestHelper testHelper;
    private final ConfigurationSynchronizationRetrievalService configurationSynchronizationRetrievalService;

    @RestClient
    private TsToJsConverterClient tsToJsConverterClient;

    @Transactional
    public void create(EnvironmentEntity environment) {
        ConfigurationSynchronizationEntity.builder()
                                          .environment(environment)
                                          .build()
                                          .persist();
    }

    @Transactional
    public List<String> synchronize(Long environmentId) throws IOException {
        log.info("Synchronize Environment id [{}].", environmentId);
        var errors = new ArrayList<String>();
        testHelper.assertNotInProgressTestByEnvironmentId(environmentId);
        var now = ZonedDateTime.now();
        var environment = environmentRetrievalService.getEnvironment(environmentId);
        var configurationSynchronization = configurationSynchronizationRetrievalService.get(environmentId);
        var repoUrl = gitlabService.getRepoUrl(environment.getToken(), environment.getProjectId());
        var projectFolder = gitlabService.cloneRepo(environment, repoUrl);
        var commitAfterDate = configurationSynchronization.getLastSynchronization() != null ? configurationSynchronization.getLastSynchronization() : ZonedDateTime.of(LocalDateTime.now().minusYears(10), ZoneId.systemDefault());

        try {
            var changedFiles = getChangedFilesAfterDate(projectFolder, commitAfterDate);
            cleanConfigurations(projectFolder, environment);
            for (var filePath : changedFiles) {
                var file = new File(projectFolder, filePath);
                if (file.exists()) {
                    var path = file.toPath();
                    var fullPath = path.toString();
                    if (fullPath.contains(START_PATH) && (fullPath.contains(END_TEST_TS_PATH) || fullPath.contains(END_TEST_JS_PATH))) {
                        var relativePathString = fullPath.split(START_PATH)[1];
                        var content = Files.readString(path);
                        if (fullPath.contains(END_TEST_TS_PATH)) {
                            content = tsToJsConverterClient.convert(content);
                        }
                        assertAndBuild(environmentId, content, relativePathString, errors);
                    }
                }
            }
            configurationSynchronization.setLastSynchronization(now);
            if (errors.isEmpty()) {
                configurationSynchronization.setStatus(SynchronizationStatus.SUCCESS);
            } else {
                configurationSynchronization.setStatus(SynchronizationStatus.FAILED);
                configurationSynchronization.setError(String.join(";", errors));
            }
        } catch (Exception e) {
            FileUtils.deleteDirectory(projectFolder);
            throw e;
        }

        FileUtils.deleteDirectory(projectFolder);
        return errors;
    }

    private void assertAndBuild(Long environmentId, String content, String relativePathString, ArrayList<String> errors) throws IOException {
        try {
            log.info("Environment id [{}] : Create or update file [{}].", environmentId, relativePathString);
            var configurationInternal = build(content, relativePathString);
            assertUniqueTitles(relativePathString, configurationInternal);
            configurationService.updateOrCreate(environmentId, relativePathString, configurationInternal);
        } catch (CustomException exception) {
            errors.add(exception.getDetail());
        }
    }

    public static void assertUniqueTitles(String path, ConfigurationInternal config) {
        checkTitles(path, config.getTests(), config.getSuites());
    }

    private static void checkTitles(String path, List<ConfigurationTestInternal> tests, List<ConfigurationSuiteInternal> suites) {
        var titles = new HashSet<String>();
        for (var test : tests) {
            if (StringUtils.isBlank(test.getTitle())) {
                throw new TitleEmptyException(path);
            }
            if (!titles.add(test.getTitle())) {
                throw new TitleDuplicationException(path, test.getTitle());
            }
            if (test.getTitle().contains("|") || test.getTitle().contains(";")) {
                throw new CharactersForbiddenException(path);
            }
        }
        for (var suite : suites) {
            if (StringUtils.isBlank(suite.getTitle())) {
                throw new TitleEmptyException(path);
            }
            if (NO_SUITE.equals(suite.getTitle())) {
                throw new SuiteNoTitleException(path);
            }
            if (!titles.add(suite.getTitle())) {
                throw new TitleDuplicationException(path, suite.getTitle());
            }
            if (suite.getTitle().contains("|") || suite.getTitle().contains(";")) {
                throw new CharactersForbiddenException(path);
            }
            checkTitles(path, suite.getTests(), suite.getSuites());
        }
    }

    //  Supprime les tests qui ne sont plus dans le repo
    private void cleanConfigurations(File projectFolder, EnvironmentEntity environment) {
        var allTestFiles = listAllTestFiles(projectFolder);
        var allConfTestFiles = environment.getConfigurationSuites().stream().map(ConfigurationSuiteEntity::getFile).toList();
        allConfTestFiles.forEach(file -> {
            if (!allTestFiles.contains(file)) {
                log.info("Environment id [{}] : Remove file [{}].", environment.getId(), file);
                configurationService.deleteConfigurationByFile(file, environment.getId());
            }
        });
    }

    private Set<String> listAllTestFiles(File parentDirectory) {
        var allTestFilePaths = new HashSet<String>();
        var subFolderPath = Paths.get(parentDirectory.getAbsolutePath(), START_PATH);
        if (Files.exists(subFolderPath) && Files.isDirectory(subFolderPath)) {
            try (var paths = Files.walk(subFolderPath)) {
                var fileList = paths.filter(Files::isRegularFile).toList();
                for (var path : fileList) {
                    int index = path.toString().indexOf(START_PATH);
                    if (index != -1) {
                        allTestFilePaths.add(path.toString().substring(index + START_PATH.length()));
                    }
                }
            } catch (IOException exception) {
                throw new ConfigurationSynchronizationException("List Files Error : " + exception.getMessage());
            }
        }
        return allTestFilePaths;
    }

    @Transactional
    public void assertEnvironmentIsNotInSync(Long environmentId) {
        var configurationSynchronization = configurationSynchronizationRetrievalService.get(environmentId);
        if (configurationSynchronization.getStatus().equals(SynchronizationStatus.IN_PROGRESS)) {
            throw new EnvironmentAlreadyInSyncProgressException();
        }
    }

    @Transactional
    public void updateSync(Long environmentId, SynchronizationStatus status, String error) {
        var configurationSynchronization = configurationSynchronizationRetrievalService.get(environmentId);
        configurationSynchronization.setStatus(status);
        configurationSynchronization.setError(error);
    }
}

