package fr.plum.e2e.manager.core.infrastructure.primary.webhook;

import fr.plum.e2e.manager.core.application.command.synchronization.StartSynchronizationCommandHandler;
import fr.plum.e2e.manager.core.application.command.worker.ReportWorkerCommandHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.command.CommonCommand;
import fr.plum.e2e.manager.core.domain.model.command.ReportWorkerCommand;
import fr.plum.e2e.manager.core.domain.model.exception.SynchronizationAlreadyInProgressException;
import fr.plum.e2e.manager.core.domain.port.repository.EnvironmentRepositoryPort;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.GitLabWebhookEvent;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.GitlabJobStatus;
import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.request.GitlabWebHookRequest;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
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
  private final ReportWorkerCommandHandler reportWorkerCommandHandler;

  private final StartSynchronizationCommandHandler startSynchronizationCommandHandler;

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
    reportWorkerCommandHandler.execute(reportWorkerCommand);
  }

  private void pushHook(GitlabWebHookRequest request) {
    log.debug(
        "Gitlab WebHook - Push Hook received for Project id [{}] and Branch name [{}].",
        request.projectId(),
        request.ref());

    var projectId = request.projectId();
    var branch = request.ref();

    var lockKey = projectId + ":" + branch;
    if (locks.putIfAbsent(lockKey, true) != null) {
      log.debug(
          "Gitlab WebHook - Synchronization is already in progress for Project id [{}] and Branch name [{}].",
          projectId,
          branch);
      return;
    }
    try {
      String finalBranch = extractRefName(branch);
      var environments = environmentRepositoryPort.findAll(projectId, finalBranch);

      environments.forEach(
          environment -> {
            try {
              startSynchronizationCommandHandler.execute(
                  CommonCommand.builder()
                      .environmentId(environment.getId())
                      .username(new ActionUsername("Gitlab Webhook"))
                      .build());
            } catch (SynchronizationAlreadyInProgressException e) {
              log.debug(
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
