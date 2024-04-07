package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.exception.RunParameterException;
import fr.njj.galaxion.endtoendtesting.domain.request.RunTestOrSuiteRequest;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.TestEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.SchedulerRepository;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationSuiteRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.configuration.ConfigurationTestRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;
import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunSuiteOrTestService {

    private final ConfigurationTestRetrievalService configurationTestRetrievalService;
    private final ConfigurationSuiteRetrievalService configurationSuiteRetrievalService;
    private final GitlabService gitlabService;
    private final SecurityIdentity identity;
    private final PipelineService pipelineService;
    private final SchedulerRepository schedulerRepository;

    @Transactional
    public void run(long environmentId, RunTestOrSuiteRequest request) {
        schedulerRepository.assertExistInProgressByEnvironment(environmentId);
        pipelineService.assertNotConcurrentJobsReached();
        assertOnlyOneParameterInRequest(request);

        String file;
        EnvironmentEntity environment;
        var configurationTests = new ArrayList<ConfigurationTestEntity>();
        StringBuilder grep = new StringBuilder();

        if (request.getConfigurationTestId() != null) {
            var configurationTest = configurationTestRetrievalService.get(request.getConfigurationTestId());
            environment = configurationTest.getEnvironment();
            file = configurationTest.getFile();
            configurationTests.add(configurationTest);
            buildSuiteGrep(configurationTest.getConfigurationSuite(), grep);
            if (StringUtils.isNotBlank(grep)) {
                grep.append(" ");
            }
            grep.append(configurationTest.getTitle());
        } else {
            var configurationSuite = configurationSuiteRetrievalService.get(request.getConfigurationSuiteId());
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
        var gitlabResponse = gitlabService.runJob(environment.getBranch(),
                                                  environment.getToken(),
                                                  environment.getProjectId(),
                                                  file,
                                                  variablesBuilder.toString(),
                                                  grep.toString(),
                                                  isVideo);

        var testIds = new ArrayList<String>();
        configurationTests.forEach(configurationTest -> {
            var test = TestEntity
                    .builder()
                    .configurationTest(configurationTest)
                    .pipelineId(gitlabResponse.getId())
                    .variables(variablesWithValueMap)
                    .createdBy(identity != null && identity.getPrincipal() != null ? identity.getPrincipal().getName() : "Unknown")
                    .build();
            test.persist();
            testIds.add(String.valueOf(test.getId()));
        });
        if (!testIds.isEmpty()) {
            pipelineService.create(environment, request.getConfigurationTestId() != null ? PipelineType.TEST : PipelineType.SUITE, gitlabResponse.getId(), testIds);
        }
    }

    private static void assertOnlyOneParameterInRequest(RunTestOrSuiteRequest request) {
        if ((request.getConfigurationTestId() != null && request.getConfigurationSuiteId() != null) ||
            (request.getConfigurationTestId() == null && request.getConfigurationSuiteId() == null)) {
            throw new RunParameterException();
        }
    }

    private void addConfigurationTestsFromSuite(ConfigurationSuiteEntity configurationSuite,
                                                List<ConfigurationTestEntity> configurationTests) {
        configurationTests.addAll(configurationSuite.getConfigurationTests());
        if (configurationSuite.getSubSuites() != null) {
            for (var subSuite : configurationSuite.getSubSuites()) {
                addConfigurationTestsFromSuite(subSuite, configurationTests);
            }
        }
    }

    private static void buildSuiteGrep(ConfigurationSuiteEntity configurationSuite, StringBuilder grep) {
        if (!NO_SUITE.equals(configurationSuite.getTitle())) {
            var titles = new ArrayList<String>();
            getTitles(titles, configurationSuite);
            Collections.reverse(titles);
            titles.forEach(title -> {
                if (StringUtils.isNotBlank(grep)) {
                    grep.append(" ");
                }
                grep.append(title);
            });
        }
    }

    private static void getTitles(List<String> titles, ConfigurationSuiteEntity configurationSuite) {
        titles.add(configurationSuite.getTitle());
        if (configurationSuite.getParentSuite() != null) {
            getTitles(titles, configurationSuite.getParentSuite());
        }
    }

    private void buildVariables(RunTestOrSuiteRequest request,
                                StringBuilder variablesBuilder,
                                HashMap<String, String> variablesWithValueMap) {
        request.getVariables().forEach(variable -> {
            variablesBuilder.append(variable.getName())
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

