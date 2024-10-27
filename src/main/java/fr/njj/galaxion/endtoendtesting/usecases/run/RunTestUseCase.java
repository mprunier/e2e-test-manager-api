package fr.njj.galaxion.endtoendtesting.usecases.run;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.event.send.RunInProgressEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.RunParameterException;
import fr.njj.galaxion.endtoendtesting.domain.request.RunTestOrSuiteRequest;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.AssertPipelineReachedService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RunGitlabJobService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.SearchSuiteRetrievalService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunTestUseCase {

  private final AssertPipelineReachedService assertPipelineReachedService;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;
  private final SearchSuiteRetrievalService searchSuiteRetrievalService;
  private final RunGitlabJobService runGitlabJobService;
  private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;

  private final Event<RunInProgressEvent> testRunInProgressEvent;

  private final CacheManager cacheManager;

  @Transactional
  public void execute(RunTestOrSuiteRequest request, String createdBy) {

    assertPipelineReachedService.assertPipeline();
    assertOnlyOneParameterInRequest(request);

    PipelineType pipelineType;
    String file;
    EnvironmentEntity environment;
    var configurationTests = new ArrayList<ConfigurationTestEntity>();
    StringBuilder grep = new StringBuilder();

    if (request.getConfigurationTestId() != null) {
      pipelineType = PipelineType.TEST;
      var configurationTest =
          configurationTestRetrievalService.get(request.getConfigurationTestId());
      environment = configurationTest.getEnvironment();
      file = configurationTest.getFile();
      configurationTests.add(configurationTest);
      buildSuiteGrep(configurationTest.getConfigurationSuite(), grep);
      if (StringUtils.isNotBlank(grep)) {
        grep.append(" ");
      }
      grep.append(configurationTest.getTitle());
    } else {
      pipelineType = PipelineType.SUITE;
      var configurationSuite = searchSuiteRetrievalService.get(request.getConfigurationSuiteId());
      environment = configurationSuite.getEnvironment();
      file = configurationSuite.getFile();
      addConfigurationTestsFromSuite(configurationSuite, configurationTests);
      buildSuiteGrep(configurationSuite, grep);
    }

    var variablesBuilder = new StringBuilder();
    var variablesWithValueMap = new HashMap<String, String>();
    buildVariables(request, variablesBuilder, variablesWithValueMap);
    buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
    var isVideo = configurationTests.size() == 1;
    var gitlabResponse =
        runGitlabJobService.runJob(
            environment.getBranch(),
            environment.getToken(),
            environment.getProjectId(),
            START_PATH + file,
            variablesBuilder.toString(),
            grep.toString(),
            null,
            isVideo);

    var configurationTestsIds =
        configurationTests.stream()
            .map(ConfigurationTestEntity::getId)
            .map(Object::toString)
            .toList();
    PipelineEntity.builder()
        .id(gitlabResponse.getId())
        .type(pipelineType)
        .environment(environment)
        .configurationTestIdsFilter(configurationTestsIds)
        .variables(variablesWithValueMap)
        .createdBy(createdBy)
        .build()
        .persist();

    cacheManager
        .getCache("in_progress_pipelines")
        .ifPresent(cache -> cache.invalidate(environment.getId()).await().indefinitely());

    buildAndSendRunInProgressEvent(environment, configurationTestsIds);
  }

  private void buildAndSendRunInProgressEvent(
      EnvironmentEntity environment, List<String> configurationTestsIds) {
    var configurationSuite =
        configurationSuiteRetrievalService.getConfigurationSuiteResponse(
            environment.getId(), Long.valueOf(configurationTestsIds.getFirst()));
    testRunInProgressEvent.fire(
        RunInProgressEvent.builder()
            .environmentId(environment.getId())
            .configurationSuite(configurationSuite)
            .build());
  }

  private void assertOnlyOneParameterInRequest(RunTestOrSuiteRequest request) {
    if ((request.getConfigurationTestId() != null && request.getConfigurationSuiteId() != null)
        || (request.getConfigurationTestId() == null
            && request.getConfigurationSuiteId() == null)) {
      throw new RunParameterException();
    }
  }

  private void addConfigurationTestsFromSuite(
      ConfigurationSuiteEntity configurationSuite,
      List<ConfigurationTestEntity> configurationTests) {
    configurationTests.addAll(configurationSuite.getConfigurationTests());
    if (configurationSuite.getSubSuites() != null) {
      for (var subSuite : configurationSuite.getSubSuites()) {
        addConfigurationTestsFromSuite(subSuite, configurationTests);
      }
    }
  }

  private void buildSuiteGrep(ConfigurationSuiteEntity configurationSuite, StringBuilder grep) {
    if (!NO_SUITE.equals(configurationSuite.getTitle())) {
      var titles = new ArrayList<String>();
      getTitles(titles, configurationSuite);
      Collections.reverse(titles);
      titles.forEach(
          title -> {
            if (StringUtils.isNotBlank(grep)) {
              grep.append(" ");
            }
            grep.append(title);
          });
    }
  }

  private void getTitles(List<String> titles, ConfigurationSuiteEntity configurationSuite) {
    titles.add(configurationSuite.getTitle());
    if (configurationSuite.getParentSuite() != null) {
      getTitles(titles, configurationSuite.getParentSuite());
    }
  }

  private void buildVariables(
      RunTestOrSuiteRequest request,
      StringBuilder variablesBuilder,
      HashMap<String, String> variablesWithValueMap) {
    request
        .getVariables()
        .forEach(
            variable -> {
              variablesBuilder
                  .append(variable.getName())
                  .append("=")
                  .append(variable.getValue())
                  .append(",");
              variablesWithValueMap.put(variable.getName(), variable.getValue());
            });
    if (request.getVariables() != null && !request.getVariables().isEmpty()) {
      variablesBuilder.deleteCharAt(variablesBuilder.length() - 1);
    }
  }
}
