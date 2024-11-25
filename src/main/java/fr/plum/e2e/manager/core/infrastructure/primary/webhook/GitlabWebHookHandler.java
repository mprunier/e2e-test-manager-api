package fr.plum.e2e.manager.core.infrastructure.primary.webhook;

import fr.plum.e2e.manager.core.application.SynchronizationFacade;
import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationAlreadyInProgressException;
import fr.plum.e2e.manager.core.domain.port.out.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.GitLabWebhookEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.GitlabJobStatus;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.request.GitlabWebHookRequest;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GitlabWebHookHandler {

  private final ConcurrentMap<String, Boolean> locks = new ConcurrentHashMap<>();

  private final EnvironmentRepositoryPort environmentRepositoryPort;
  private final SynchronizationFacade synchronizationFacade;
  private final WorkerFacade workerFacade;

  public void gitlabCallback(String gitlabEvent, GitlabWebHookRequest request) {
    var event = GitLabWebhookEvent.fromHeaderValue(gitlabEvent);
    if (GitLabWebhookEvent.JOB_HOOK.equals(event)) {
      jobHook(request);
    } else if (GitLabWebhookEvent.PUSH_HOOK.equals(event)) {
      pushHook(request);
    }
  }

  private void jobHook(GitlabWebHookRequest request) {
    log.trace(
        "Gitlab WebHook - Job Hook received with worker [{}] and status [{}].",
        request.pipelineId(),
        request.status());

    var isFinish = request.finishedAt() != null;
    if (!isFinish) {
      return;
    }

    var status = GitlabJobStatus.fromHeaderValue(request.status());
    if (status == GitlabJobStatus.created
        || status == GitlabJobStatus.pending
        || status == GitlabJobStatus.running) {
      return;
    }

    var pipelineId = request.pipelineId();

    var reportWorkerCommand =
        ReportWorkerCommand.builder().workerUnitId(new WorkerUnitId(pipelineId)).build();
    workerFacade.report(reportWorkerCommand);
  }

  private void pushHook(GitlabWebHookRequest request) {
    log.trace(
        "Gitlab WebHook - Push Hook received for Project id [{}] and Branch name [{}].",
        request.projectId(),
        request.ref());

    var projectId = request.projectId();
    var branch = request.ref();

    var lockKey = projectId + ":" + branch;
    if (locks.putIfAbsent(lockKey, true) != null) {
      log.trace(
          "Gitlab WebHook - Synchronization is already in progress for Project id [{}] and Branch name [{}].",
          projectId,
          branch);
      return;
    }
    try {
      String finalBranch = extractRefName(branch);
      var environments =
          environmentRepositoryPort.findAllByProjectIdAndBranch(projectId, finalBranch);

      environments.forEach(
          environment -> {
            try {
              synchronizationFacade.startSynchronization(
                  CommonCommand.builder()
                      .environmentId(environment.getId())
                      .username(new ActionUsername("Gitlab Webhook"))
                      .build());
            } catch (SynchronizationAlreadyInProgressException e) {
              log.trace(
                  "Gitlab WebHook - Synchronization is already in progress for Project id [{}] and Branch name [{}] on environment id [{}].",
                  projectId,
                  finalBranch,
                  environment.getId().value());
            }
          });
    } finally {
      locks.remove(lockKey);
    }
  }

  private static String extractRefName(final String ref) {
    if (ref != null && (ref.startsWith("refs/heads/") || ref.startsWith("refs/tags/"))) {
      String[] parts = ref.split("/");
      return parts[parts.length - 1];
    }
    return ref;
  }
}
