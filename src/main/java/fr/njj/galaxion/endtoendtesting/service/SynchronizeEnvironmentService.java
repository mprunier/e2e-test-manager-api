package fr.njj.galaxion.endtoendtesting.service;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_JS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.END_TEST_TS_PATH;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.mapper.ConfigurationInternalMapper.build;

import fr.njj.galaxion.endtoendtesting.client.converter.ConverterClient;
import fr.njj.galaxion.endtoendtesting.domain.exception.CharactersForbiddenException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SuiteNoTitleException;
import fr.njj.galaxion.endtoendtesting.domain.exception.SuiteShouldBeNotContainsSubSuiteException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleDuplicationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.TitleEmptyException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationSuiteInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.ConfigurationTestInternal;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestTagEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.FileGroupEntity;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.FileGroupRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.SearchSuiteRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SynchronizeEnvironmentService {

  private final EnvironmentRetrievalService environmentRetrievalService;
  private final DeleteConfigurationTestAndSuiteService deleteConfigurationTestAndSuiteService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final SearchSuiteRetrievalService searchSuiteRetrievalService;
  private final DeleteFileGroupService deleteFileGroupService;
  private final FileGroupRetrievalService fileGroupRetrievalService;

  @RestClient private ConverterClient converterClient;

  public void synchronize(
      EnvironmentEntity environment,
      Set<String> changedFiles,
      File projectFolder,
      Map<String, String> errors)
      throws IOException {
    for (var filePath : changedFiles) {
      var file = new File(projectFolder, filePath);
      if (file.exists()) {
        var path = file.toPath();
        var fullPath = path.toString();
        if (fullPath.contains(START_PATH)
            && (fullPath.contains(END_TEST_TS_PATH) || fullPath.contains(END_TEST_JS_PATH))) {
          var relativePathString = fullPath.split(START_PATH)[1];
          var content = Files.readString(path);
          boolean hasConverterError = false;
          if (fullPath.contains(END_TEST_TS_PATH)) {
            try {
              content = converterClient.convertTs(content);
            } catch (Exception exception) {
              errors.put(
                  filePath,
                  "Error during the transpilation of TypeScript code into JavaScript. Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.");
              log.error(
                  "Error during the transpilation of TypeScript code into JavaScript on file [{}] and Environment id [{}]",
                  filePath,
                  environment.getId());
              hasConverterError = true;
            }
          }
          try {
            content = converterClient.transpileJs(content);
          } catch (Exception exception) {
            errors.put(
                filePath,
                "Error during the transpilation of JavaScript code to ES6. Please ensure that your code is correctly formatted as JavaScript or TypeScript without any errors.");
            log.error(
                "Error during the transpilation of JavaScript code to ES6 on file [{}] and Environment id [{}]",
                filePath,
                environment.getId());
            hasConverterError = true;
          }

          if (!hasConverterError) {
            assertAndBuild(environment.getId(), content, relativePathString, errors, filePath);
          }
        }
      }
    }
  }

  private void assertAndBuild(
      Long environmentId,
      String content,
      String relativePathString,
      Map<String, String> errors,
      String filePath) {
    try {
      var configurationInternal = build(content, relativePathString);
      assertNoSubSuiteInTestSuite(configurationInternal);
      assertUniqueTitles(configurationInternal);
      updateOrCreate(environmentId, relativePathString, configurationInternal);
    } catch (CustomException exception) {
      errors.put(filePath, exception.getDetail());
    }
  }

  // Always managed by the api but not by the front so we block here for the moment. To see if clean
  // to not manage it on the api side, is not cleaner. (TODO)
  private static void assertNoSubSuiteInTestSuite(ConfigurationInternal configurationInternal) {
    configurationInternal
        .getSuites()
        .forEach(
            suite -> {
              if (suite.isExistSubSuite()) {
                throw new SuiteShouldBeNotContainsSubSuiteException();
              }
            });
  }

  private static void assertUniqueTitles(ConfigurationInternal config) {
    checkTitles(config.getTests(), config.getSuites());
  }

  private static void checkTitles(
      List<ConfigurationTestInternal> tests, List<ConfigurationSuiteInternal> suites) {
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

  private void updateOrCreate(
      Long environmentId, String file, ConfigurationInternal configurationInternal) {
    var environment = environmentRetrievalService.get(environmentId);

    var suiteIds = new ArrayList<Long>();
    var testIds = new ArrayList<Long>();

    if (!configurationInternal.getTests().isEmpty()) {
      configurationInternal
          .getTests()
          .forEach(
              testInternal ->
                  updateOrCreateTestWithoutSuite(
                      environment, file, testInternal, suiteIds, testIds));
    }
    if (!configurationInternal.getSuites().isEmpty()) {
      configurationInternal
          .getSuites()
          .forEach(
              suiteInternal ->
                  updateOrCreateSuite(environment, file, suiteInternal, null, suiteIds, testIds));
    }

    deleteConfigurationTestAndSuiteService.deleteTestByEnvAndFileAndNotInTestIds(
        environmentId, file, testIds);
    deleteConfigurationTestAndSuiteService.deleteSuiteByEnvAndFileAndNotInTestIds(
        environmentId, file, suiteIds);

    deleteOrCreateFileGroup(
        environmentId, file, configurationInternal, suiteIds, testIds, environment);
  }

  private void deleteOrCreateFileGroup(
      Long environmentId,
      String file,
      ConfigurationInternal configurationInternal,
      ArrayList<Long> suiteIds,
      ArrayList<Long> testIds,
      EnvironmentEntity environment) {
    if (suiteIds.isEmpty() && testIds.isEmpty()) {
      deleteFileGroupService.deleteByEnvAndFile(environmentId, file);
    } else {
      var fileGroup = fileGroupRetrievalService.getOptionalFileGroup(environmentId, file);
      if (fileGroup.isEmpty() && StringUtils.isNotBlank(configurationInternal.getGroup())) {
        FileGroupEntity.builder()
            .environment(environment)
            .file(file)
            .group(configurationInternal.getGroup())
            .build()
            .persist();
      } else if (fileGroup.isPresent() && StringUtils.isBlank(configurationInternal.getGroup()))
        deleteFileGroupService.deleteByEnvAndFile(environmentId, file);
    }
  }

  private void updateOrCreateTestWithoutSuite(
      EnvironmentEntity environment,
      String file,
      ConfigurationTestInternal testInternal,
      List<Long> suiteIds,
      List<Long> testIds) {
    ConfigurationSuiteEntity configurationSuite;
    var configurationSuiteOptional =
        searchSuiteRetrievalService.getBy(environment.getId(), file, NO_SUITE, null);
    if (configurationSuiteOptional.isEmpty()) {
      configurationSuite =
          ConfigurationSuiteEntity.builder()
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

  private void updateOrCreateSuite(
      EnvironmentEntity environment,
      String file,
      ConfigurationSuiteInternal suiteInternal,
      ConfigurationSuiteEntity parentSuite,
      List<Long> suiteIds,
      List<Long> testIds) {
    ConfigurationSuiteEntity configurationSuite;
    var parentSuiteId = parentSuite != null ? parentSuite.getId() : null;
    var configurationSuiteOptional =
        searchSuiteRetrievalService.getBy(
            environment.getId(), file, suiteInternal.getTitle(), parentSuiteId);
    if (configurationSuiteOptional.isEmpty()) {
      configurationSuite =
          ConfigurationSuiteEntity.builder()
              .environment(environment)
              .title(suiteInternal.getTitle())
              .file(file)
              .parentSuite(parentSuite)
              .variables(
                  CollectionUtils.isEmpty(suiteInternal.getVariables())
                      ? null
                      : suiteInternal.getVariables())
              .build();
      configurationSuite.persist();
    } else {
      configurationSuite = configurationSuiteOptional.get();
      configurationSuite.setUpdatedAt(ZonedDateTime.now());
      configurationSuite.setVariables(
          CollectionUtils.isEmpty(suiteInternal.getVariables())
              ? null
              : suiteInternal.getVariables());
    }
    suiteIds.add(configurationSuite.getId());

    suiteInternal
        .getTests()
        .forEach(
            testInternal ->
                updateOrCreateTest(environment, file, testInternal, configurationSuite, testIds));
    suiteInternal
        .getSuites()
        .forEach(
            suiteInternalChild ->
                updateOrCreateSuite(
                    environment, file, suiteInternalChild, configurationSuite, suiteIds, testIds));
  }

  private void updateOrCreateTest(
      EnvironmentEntity environment,
      String file,
      ConfigurationTestInternal testInternal,
      ConfigurationSuiteEntity configurationSuite,
      List<Long> testIds) {
    ConfigurationTestEntity configurationTest;
    var configurationTestOptional =
        configurationTestRetrievalService.getBy(
            environment.getId(), file, testInternal.getTitle(), configurationSuite);

    var configurationTestTagEntities = new ArrayList<ConfigurationTestTagEntity>();
    if (configurationTestOptional.isEmpty()) {
      configurationTest =
          ConfigurationTestEntity.builder()
              .environment(environment)
              .configurationSuite(configurationSuite)
              .title(testInternal.getTitle())
              .file(file)
              .variables(
                  CollectionUtils.isEmpty(testInternal.getVariables())
                      ? null
                      : testInternal.getVariables())
              .build();
      configurationTest.persist();
      testInternal
          .getTags()
          .forEach(
              tag -> {
                var configurationTestTagEntity =
                    ConfigurationTestTagEntity.builder()
                        .configurationTest(configurationTest)
                        .tag(tag)
                        .environmentId(configurationTest.getEnvironment().getId())
                        .build();
                configurationTestTagEntities.add(configurationTestTagEntity);
                configurationTestTagEntity.persist();
              });
      configurationTest.setConfigurationTags(configurationTestTagEntities);
    } else {
      configurationTest = configurationTestOptional.get();
      configurationTest.getConfigurationTags().clear();
      testInternal
          .getTags()
          .forEach(
              tag -> {
                var configurationTestTagEntity =
                    ConfigurationTestTagEntity.builder()
                        .configurationTest(configurationTest)
                        .tag(tag)
                        .environmentId(configurationTest.getEnvironment().getId())
                        .build();
                configurationTestTagEntities.add(configurationTestTagEntity);
                configurationTestTagEntity.persist();
              });
      configurationTest.setVariables(
          CollectionUtils.isEmpty(testInternal.getVariables())
              ? null
              : testInternal.getVariables());
      configurationTest.setUpdatedAt(ZonedDateTime.now());
    }
    testIds.add(configurationTest.getId());
  }
}
