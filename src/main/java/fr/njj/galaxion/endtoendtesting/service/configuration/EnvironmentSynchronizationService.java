package fr.njj.galaxion.endtoendtesting.service.configuration;

import fr.njj.galaxion.endtoendtesting.client.converter.ConverterClient;
import fr.njj.galaxion.endtoendtesting.domain.exception.CharactersForbiddenException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SuiteNoTitleException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleDuplicationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleEmptyException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationTestInternal;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.EnvironmentSynchronizationErrorRepository;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_JS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_TS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.GLOBAL_ENVIRONMENT_ERROR;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationInternalMapper.build;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class EnvironmentSynchronizationService {

    private final ConfigurationService configurationService;
    private final EnvironmentSynchronizationErrorRepository environmentSynchronizationErrorRepository;

    @RestClient
    private ConverterClient converterClient;

    public void synchronize(EnvironmentEntity environment, Set<String> changedFiles, File projectFolder, HashMap<String, String> errors) throws IOException {
        for (var filePath : changedFiles) {
            var file = new File(projectFolder, filePath);
            if (file.exists()) {
                var path = file.toPath();
                var fullPath = path.toString();
                if (fullPath.contains(START_PATH) && (fullPath.contains(END_TEST_TS_PATH) || fullPath.contains(END_TEST_JS_PATH))) {
                    var relativePathString = fullPath.split(START_PATH)[1];
                    var content = Files.readString(path);
                    boolean hasError = false;
                    if (fullPath.contains(END_TEST_TS_PATH)) {
                        try {
                            content = converterClient.convertTs(content);
                        } catch (Exception exception) {
                            errors.put(filePath, "Error during the transpilation of TypeScript code into JavaScript. Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.");
                            log.error("Error during the transpilation of TypeScript code into JavaScript on file [{}] and Environment id [{}]", filePath, environment.getId());
                            hasError = true;
                        }
                    }
                    try {
                        content = converterClient.transpileJs(content);
                    } catch (Exception exception) {
                        errors.put(filePath, "Error during the transpilation of JavaScript code to ES6. Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.");
                        log.error("Error during the transpilation of JavaScript code to ES6 on file [{}] and Environment id [{}]", filePath, environment.getId());
                        hasError = true;
                    }

                    if (!hasError) {
                        assertAndBuild(environment.getId(), content, relativePathString, errors, filePath);
                    }
                }
            }
        }
    }

    private void assertAndBuild(Long environmentId, String content, String relativePathString, HashMap<String, String> errors, String filePath) {
        try {
            var configurationInternal = build(content, relativePathString);
            assertUniqueTitles(configurationInternal);
            configurationService.updateOrCreate(environmentId, relativePathString, configurationInternal);
        } catch (CustomException exception) {
            errors.put(filePath, exception.getDetail());
        }
    }

    private static void assertUniqueTitles(ConfigurationInternal config) {
        checkTitles(config.getTests(), config.getSuites());
    }

    private static void checkTitles(List<ConfigurationTestInternal> tests, List<ConfigurationSuiteInternal> suites) {
        var titles = new HashSet<String>();
        for (var test : tests) {
            if (StringUtils.isBlank(test.getTitle())) {
                throw new TitleEmptyException();
            }
            if (!titles.add(test.getTitle())) {
                throw new TitleDuplicationException(test.getTitle());
            }
            if (test.getTitle().contains("|") || test.getTitle().contains(";")) {
                throw new CharactersForbiddenException();
            }
        }
        for (var suite : suites) {
            if (StringUtils.isBlank(suite.getTitle())) {
                throw new TitleEmptyException();
            }
            if (NO_SUITE.equals(suite.getTitle())) {
                throw new SuiteNoTitleException();
            }
            if (!titles.add(suite.getTitle())) {
                throw new TitleDuplicationException(suite.getTitle());
            }
            if (suite.getTitle().contains("|") || suite.getTitle().contains(";")) {
                throw new CharactersForbiddenException();
            }
            checkTitles(suite.getTests(), suite.getSuites());
        }
    }

    public static void cleanRepo(EnvironmentEntity environment, File projectFolder, Map<String, String> errors) {
        try {
            FileUtils.deleteDirectory(projectFolder);
        } catch (IOException exception) {
            errors.put(GLOBAL_ENVIRONMENT_ERROR, exception.getMessage());
            log.error("Error during remove repository for Environment id [{}] : {}.", environment.getId(), exception.getMessage());
        }
    }

    @Transactional
    public void cleanErrors(
            long environmentId,
            String file) {

        if (file != null) {
            environmentSynchronizationErrorRepository.deleteByEnvironmentIdAndFile(environmentId, file);
        } else {
            environmentSynchronizationErrorRepository.deleteByEnvironmentId(environmentId);
        }
    }
}

