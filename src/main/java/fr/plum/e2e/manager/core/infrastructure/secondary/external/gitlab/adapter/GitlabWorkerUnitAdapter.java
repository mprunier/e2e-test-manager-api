package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.adapter;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.mapper.GitlabPipelineRequestMapper.buildPipelineRequest;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.Environment;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerIsRecordVideo;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitFilter;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import fr.plum.e2e.manager.core.domain.port.WorkerUnitPort;
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
      Environment environment,
      WorkerUnitFilter workerUnitFilter,
      List<WorkerVariable> workerVariables,
      WorkerIsRecordVideo workerIsRecordVideo) {

    var pipelineRequest =
        buildPipelineRequest(environment, workerUnitFilter, workerVariables, workerIsRecordVideo);

    var gitlabResponse =
        gitlabClient.runPipeline(
            environment.getSourceCodeInformation().token(),
            environment.getSourceCodeInformation().projectId(),
            pipelineRequest);
    return new WorkerUnitId(gitlabResponse.getId());
  }

  @Override
  public WorkerUnitStatus getWorkerStatus(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    var job = gitlabJobHandler.getJobId(sourceCodeInformation, workerUnitId);
    return job.getStatus().toWorkerStatus();
  }

  @Override
  public Object getWorkerReportArtifacts(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    var jobId = gitlabJobHandler.getJobId(sourceCodeInformation, workerUnitId).getId();
    return gitlabClient.getJobArtifacts(
        sourceCodeInformation.token(), sourceCodeInformation.projectId(), jobId);
  }

  @Override
  public void cancel(SourceCodeInformation sourceCodeInformation, WorkerUnitId id) {
    gitlabClient.cancelPipeline(
        sourceCodeInformation.token(), sourceCodeInformation.projectId(), id.value());
  }
}
