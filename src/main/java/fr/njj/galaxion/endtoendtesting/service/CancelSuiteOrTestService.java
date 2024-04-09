package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelSuiteOrTestService {

    private final TestRetrievalService testRetrievalService;
    private final GitlabService gitlabService;

    @Transactional
    public void cancel(String pipelineId, List<String> testStrIds) {
        var testIds = testStrIds.stream().map(Long::valueOf).toList();

        var tests = testRetrievalService.getAll(testIds);
        var environment = tests.getFirst().getConfigurationTest().getEnvironment();
        gitlabService.cancelPipeline(environment.getToken(), environment.getProjectId(), pipelineId);
        updateStatus(tests, ConfigurationStatus.CANCELED);
    }
}

