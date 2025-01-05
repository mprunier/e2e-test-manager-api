package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.adapter;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_GITLAB_JOB;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.domain.response.GitlabResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.exception.MoreOneJobException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
public class GitlabJobHandler {

  @RestClient GitlabClient gitlabClient;

  @CacheName(CACHE_GITLAB_JOB)
  Cache cache;

  public GitlabResponse getJobId(SourceCodeInformation sourceCodeInformation, WorkerUnitId id) {
    var cacheKey =
        String.format(
            "%s-%s-%s",
            sourceCodeInformation.projectId(), sourceCodeInformation.token(), id.value());

    var response =
        cache
            .get(cacheKey, k -> fetchJobFromGitlab(sourceCodeInformation, id))
            .await()
            .indefinitely();

    if (response.getStatus().toWorkerStatus() == WorkerUnitStatus.IN_PROGRESS) {
      cache.invalidate(cacheKey).await().indefinitely();
    }

    return response;
  }

  private GitlabResponse fetchJobFromGitlab(
      SourceCodeInformation sourceCodeInformation, WorkerUnitId workerUnitId) {
    try {
      var jobs =
          gitlabClient.getJobs(
              sourceCodeInformation.token(),
              sourceCodeInformation.projectId(),
              workerUnitId.value());
      if (jobs.size() > 1) {
        throw new MoreOneJobException(workerUnitId.value());
      }
      return jobs.getFirst();
    } catch (CustomException e) {
      log.error("Error during fetching job from Gitlab.");
      throw e;
    }
  }
}
