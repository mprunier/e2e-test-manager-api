package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.ReportAllTestRanStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.UpdateFinalMetricsEvent;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.ReportAllTestsService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.run.AllTestsRunCompletedUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.run.TestRunCompletedUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RecordResultPipelineUseCase {

    private final PipelineRetrievalService pipelineRetrievalService;
    private final TestRetrievalService testRetrievalService;
    private final GitlabService gitlabService;
    private final GenerateTestReportUseCase generateTestReportUseCase;
    private final ReportAllTestsService reportAllTestsService;
    private final AllTestsRunCompletedUseCase allTestsRunCompletedUseCase;
    private final TestRunCompletedUseCase testRunCompletedUseCase;

    private final Event<UpdateFinalMetricsEvent> updateFinalMetricsEvent;

    @Monitored(logExit = false)
    @Transactional
    public void execute(
            String pipelineId,
            String jobId,
            GitlabJobStatus status) {

        var pipeline = pipelineRetrievalService.get(pipelineId);
        var environment = pipeline.getEnvironment();

        var isAllTestsRun = pipeline.getTestIds() == null;

        if (isAllTestsRun) {
            globalUpdate(pipeline, status, environment, jobId);
        } else {
            partialUpdate(jobId, status, pipeline, environment);
        }
        pipeline.setStatus(PipelineStatus.FINISH);
        updateFinalMetricsEvent.fire(UpdateFinalMetricsEvent.builder().environmentId(environment.getId()).isAllTestsRun(isAllTestsRun).build());
    }

    private void globalUpdate(PipelineEntity pipeline, GitlabJobStatus status, EnvironmentEntity environment, String jobId) {
        var environmentId = pipeline.getEnvironment().getId();
        try {
            if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), jobId);
                if (artifactData.getReport() != null && artifactData.getReport().getResults() != null && !artifactData.getReport().getResults().isEmpty()) {
                    reportAllTestsService.report(artifactData, environmentId);
                    allTestsRunCompletedUseCase.execute(environmentId, null);
                } else {
                    allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.NO_REPORT_ERROR);
                }
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.CANCELED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            allTestsRunCompletedUseCase.execute(environmentId, ReportAllTestRanStatus.SYSTEM_ERROR);
        }
    }

    private void partialUpdate(String jobId, GitlabJobStatus status, PipelineEntity pipeline, EnvironmentEntity environment) {
        var testIds = pipeline.getTestIds().stream().map(Long::valueOf).toList();
        var tests = testRetrievalService.getAll(testIds);

        try {
            if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), jobId);
                generateTestReportUseCase.execute(artifactData, tests);
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                updateStatus(tests, ConfigurationStatus.CANCELED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR);
        } finally {
            testRunCompletedUseCase.execute(environment.getId());
        }
    }
}

