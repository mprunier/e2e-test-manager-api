package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.adapter;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.mapper.GitlabPipelineRequestMapper.buildPipelineRequest;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.port.out.WorkerUnitPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GitlabWorkerUnitAdapter implements WorkerUnitPort {

  @RestClient private GitlabClient gitlabClient;

  private final GitlabJobHandler gitlabJobHandler;

  @Override
  public WorkerUnitId runWorker(
      SourceCodeInformation sourceCodeInformation,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {

    var pipelineRequest =
        buildPipelineRequest(
            sourceCodeInformation, workerUnitFilter, workerVariables, workerIsRecordVideo);

    var gitlabResponse =
        gitlabClient.runPipeline(
            sourceCodeInformation.sourceCodeToken().value(),
            sourceCodeInformation.sourceCodeProjectId().value(),
            pipelineRequest);
    return new WorkerUnitId(gitlabResponse.getId());
  }

  @Override
  public WorkerUnitStatus getWorkerStatus(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    var job =
        gitlabJobHandler.getJobId(
            sourceCodeInformation.sourceCodeProjectId(),
            sourceCodeInformation.sourceCodeToken(),
            workerUnitId);
    return job.getStatus().toWorkerStatus();
  }

  @Override
  public Object getWorkerReportArtifacts(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    var jobId =
        gitlabJobHandler
            .getJobId(
                sourceCodeInformation.sourceCodeProjectId(),
                sourceCodeInformation.sourceCodeToken(),
                workerUnitId)
            .getId();
    return gitlabClient.getJobArtifacts(
        sourceCodeInformation.sourceCodeToken().value(),
        sourceCodeInformation.sourceCodeProjectId().value(),
        jobId);
  }

  @Override
  public void cancel(SourceCodeInformation sourceCodeInformation, WorkerUnitId id) {
    gitlabClient.cancelPipeline(
        sourceCodeInformation.sourceCodeToken().value(),
        sourceCodeInformation.sourceCodeProjectId().value(),
        id.value());
  }
}
