package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
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
public class UpdateSuiteOrTestService {

    private final TestRetrievalService testRetrievalService;
    private final GitlabService gitlabService;
    private final ReportSuiteOrTestService reportSuiteOrTestService;

    @Transactional
    public boolean update(String pipelineId, List<String> testStrIds) {
        var testIds = testStrIds.stream().map(Long::valueOf).toList();

        var title = testStrIds.size() > 1 ? "Suite Tests" : "Test";

        try {
            var environment = testRetrievalService.get(testIds.get(0)).getConfigurationTest().getEnvironment();

            var gitlabJobLogsResponse = gitlabService.getJob(environment.getToken(), environment.getProjectId(), pipelineId);

            if (GitlabJobStatus.success.name().equals(gitlabJobLogsResponse.getStatus()) ||
                GitlabJobStatus.failed.name().equals(gitlabJobLogsResponse.getStatus())) {
                log.info("Update {} on Job id [{}]. Status is [{}].", title, pipelineId, gitlabJobLogsResponse.getStatus());
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), gitlabJobLogsResponse.getId());
                var tests = testRetrievalService.getAll(testIds);
                if (artifactData.getReport() != null) {
                    reportSuiteOrTestService.report(artifactData, tests);
                } else {
                    updateStatus(tests, ConfigurationStatus.NO_REPORT_ERROR, true);
                }
                return true;

            } else if (GitlabJobStatus.canceled.name().equals(gitlabJobLogsResponse.getStatus()) ||
                       GitlabJobStatus.skipped.name().equals(gitlabJobLogsResponse.getStatus())) {
                log.info("Update {} on Job id [{}]. Status is [{}].", title, pipelineId, gitlabJobLogsResponse.getStatus());
                var tests = testRetrievalService.getAll(testIds);
                updateStatus(tests, ConfigurationStatus.CANCELED, false);
                return true;

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            var tests = testRetrievalService.getAll(testIds);
            updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR, true);
            return true;
        }
        return false;
    }
}

