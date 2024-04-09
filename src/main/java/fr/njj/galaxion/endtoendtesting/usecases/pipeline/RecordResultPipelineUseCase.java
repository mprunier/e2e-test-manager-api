package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.SchedulerStatus;
import fr.njj.galaxion.endtoendtesting.lib.logging.Monitored;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.ReportSchedulerService;
import fr.njj.galaxion.endtoendtesting.service.ReportSuiteOrTestService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.AddMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.scheduler.UpdateSchedulerStatusUseCase;
import jakarta.enterprise.context.ApplicationScoped;
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
    private final ReportSuiteOrTestService reportSuiteOrTestService;
    private final ReportSchedulerService reportSchedulerService;
    private final UpdateSchedulerStatusUseCase updateSchedulerStatusUseCase;
    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;
    private final AddMetricsUseCase addMetricsUseCase;

    @Monitored
    @Transactional
    public void execute(
            String pipelineId,
            String jobId,
            GitlabJobStatus status) {

        var pipeline = pipelineRetrievalService.get(pipelineId);
        var environment = pipeline.getEnvironment();

        if (pipeline.getTestIds() != null) {
            partialUpdate(jobId, status, pipeline, environment);
        } else {
            globalUpdate(pipeline, status, environment, jobId);
        }
        pipeline.setStatus(PipelineStatus.FINISH);
        finalizeMetrics(pipeline.getEnvironment().getId());
    }

    private void globalUpdate(PipelineEntity pipeline, GitlabJobStatus status, EnvironmentEntity environment, String jobId) {
        var environmentId = pipeline.getEnvironment().getId();
        try {
            if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), jobId);
                if (artifactData.getReport() != null) {
                    reportSchedulerService.report(artifactData, environmentId);
                    updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.SUCCESS);
                } else {
                    updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.NO_REPORT_ERROR);
                }
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.CANCELED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            updateSchedulerStatusUseCase.execute(environmentId, SchedulerStatus.SYSTEM_ERROR);
        }
    }

    private void partialUpdate(String jobId, GitlabJobStatus status, PipelineEntity pipeline, EnvironmentEntity environment) {
        var testIds = pipeline.getTestIds().stream().map(Long::valueOf).toList();
        var tests = testRetrievalService.getAll(testIds);

        try {
            if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), jobId);
                if (artifactData.getReport() != null) {
                    reportSuiteOrTestService.report(artifactData, tests);
                } else {
                    updateStatus(tests, ConfigurationStatus.NO_REPORT_ERROR);
                }
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                updateStatus(tests, ConfigurationStatus.CANCELED);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR);
        }
    }

    private void finalizeMetrics(Long environmentId) {
        var finalMetrics = calculateFinalMetricsUseCase.execute(environmentId);
        addMetricsUseCase.execute(environmentId, finalMetrics, true);
    }
}

