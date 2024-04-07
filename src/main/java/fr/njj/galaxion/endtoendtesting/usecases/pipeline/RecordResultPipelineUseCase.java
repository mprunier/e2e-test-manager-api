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
import fr.njj.galaxion.endtoendtesting.service.SchedulerRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import fr.njj.galaxion.endtoendtesting.service.test.TestRetrievalService;
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
    private final SchedulerRetrievalService schedulerRetrievalService;

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
    }

    private void globalUpdate(PipelineEntity pipeline, GitlabJobStatus status, EnvironmentEntity environment, String jobId) {
        var scheduler = schedulerRetrievalService.getSchedulerByPipelineId(pipeline.getId());
        try {
            if (GitlabJobStatus.success.equals(status) || GitlabJobStatus.failed.equals(status)) {
                var artifactData = gitlabService.getArtifactData(environment.getToken(), environment.getProjectId(), jobId);
                if (artifactData.getReport() != null) {
                    reportSchedulerService.report(artifactData, scheduler);
                    // TODO enregistrer les résulatst dans une table
                } else {
                    scheduler.setStatus(SchedulerStatus.NO_REPORT_ERROR);
                }
                pipeline.setStatus(PipelineStatus.FINISH);
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                scheduler.setStatus(SchedulerStatus.CANCELED);
                pipeline.setStatus(PipelineStatus.FINISH);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            scheduler.setStatus(SchedulerStatus.SYSTEM_ERROR);
            pipeline.setStatus(PipelineStatus.FINISH);
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
                pipeline.setStatus(PipelineStatus.FINISH);
            } else if (GitlabJobStatus.canceled.equals(status) || GitlabJobStatus.skipped.equals(status)) {
                updateStatus(tests, ConfigurationStatus.CANCELED);
                pipeline.setStatus(PipelineStatus.FINISH);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            updateStatus(tests, ConfigurationStatus.SYSTEM_ERROR);
            pipeline.setStatus(PipelineStatus.FINISH);
        }
    }
}

