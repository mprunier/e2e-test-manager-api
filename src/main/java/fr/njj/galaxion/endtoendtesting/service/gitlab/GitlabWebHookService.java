package fr.njj.galaxion.endtoendtesting.service.gitlab;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitLabWebhookEvent;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.GitlabJobStatus;
import fr.njj.galaxion.endtoendtesting.domain.request.webhook.GitlabWebHookRequest;
import fr.njj.galaxion.endtoendtesting.service.PipelineRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.usecases.metrics.CalculateFinalMetricsUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.pipeline.RecordResultPipelineUseCase;
import fr.njj.galaxion.endtoendtesting.usecases.synchronisation.PartialEnvironmentSynchronizationUseCase;
import fr.njj.galaxion.endtoendtesting.websocket.events.SyncErrorEventService;
import fr.njj.galaxion.endtoendtesting.websocket.events.UpdateFinalMetricsEventService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GitlabWebHookService {

    private final ConcurrentMap<String, Boolean> locks = new ConcurrentHashMap<>();

    private final PartialEnvironmentSynchronizationUseCase partialEnvironmentSynchronizationUseCase;
    private final RecordResultPipelineUseCase recordResultPipelineUseCase;
    private final SyncErrorEventService syncErrorEventService;
    private final EnvironmentRetrievalService environmentRetrievalService;
    private final UpdateFinalMetricsEventService updateFinalMetricsEventService;
    private final CalculateFinalMetricsUseCase calculateFinalMetricsUseCase;
    private final PipelineRetrievalService pipelineRetrievalService;

    public void gitlabCallback(String gitlabEvent, GitlabWebHookRequest request) {
        var event = GitLabWebhookEvent.fromHeaderValue(gitlabEvent);
        if (GitLabWebhookEvent.JOB_HOOK.equals(event)) {
            jobHook(request);
        } else if (GitLabWebhookEvent.PUSH_HOOK.equals(event)) {
            pushHook(request);
        }
    }

    private void jobHook(GitlabWebHookRequest request) {

        var isFinish = request.getFinishedAt() != null;
        if (!isFinish) {
            return;
        }

        var status = GitlabJobStatus.fromHeaderValue(request.getStatus());
        if (status == GitlabJobStatus.created || status == GitlabJobStatus.pending || status == GitlabJobStatus.running) {
            return;
        }

        var pipelineId = request.getPipelineId();
        var jobId = request.getJobId();

        recordResultPipelineUseCase.execute(pipelineId, jobId, status);
        buildAndSendFinalMetricsEvent(pipelineId);
    }

    private void buildAndSendFinalMetricsEvent(String pipelineId) {
        var pipeline = pipelineRetrievalService.get(pipelineId);
        var environment = pipeline.getEnvironment();
        var finalMetrics = calculateFinalMetricsUseCase.execute(environment.getId());
        updateFinalMetricsEventService.send(environment.getId(), finalMetrics);
    }

    private void pushHook(GitlabWebHookRequest request) {
        var projectId = request.getProjectId();
        var branch = request.getRef();

        var lockKey = projectId + ":" + branch;
        if (locks.putIfAbsent(lockKey, true) != null) {
            log.trace("Gitlab WebHook - Synchronization is already in progress for Project id [{}] and Branch name [{}].", projectId, branch);
            return;
        }
        try {
            var filesToRemove = new HashSet<String>();
            var filesToSynchronize = new HashSet<String>();

            request.getCommits().forEach(commit -> {
                filesToSynchronize.addAll(commit.getAdded());
                filesToSynchronize.addAll(commit.getModified());
                filesToSynchronize.addAll(commit.getRemoved());
            });
            partialEnvironmentSynchronizationUseCase.execute(projectId, branch, filesToSynchronize, filesToRemove);
        } finally {
            var environments = environmentRetrievalService.getEnvironmentsByBranchAndProjectId(branch, projectId);
            environments.forEach(environment -> syncErrorEventService.send(environment.getId()));
            locks.remove(lockKey);
        }
    }
}
