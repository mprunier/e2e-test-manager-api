package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.adapter;

import static fr.plum.e2e.manager.sharedkernel.infrastructure.cache.CacheNamesConstant.CACHE_GITLAB_JOB;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeProjectId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.sourcecode.SourceCodeToken;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.domain.response.GitlabResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.exception.MoreOneJobException;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class GitlabJobHandler {
  @RestClient GitlabClient gitlabClient;

  @CacheName(CACHE_GITLAB_JOB)
  Cache cache;

  public GitlabResponse getJobId(
      SourceCodeProjectId projectId, SourceCodeToken token, WorkerUnitId id) {
    var cacheKey = String.format("%s-%s-%s", projectId.value(), token.value(), id.value());

    var response =
        cache.get(cacheKey, k -> fetchJobFromGitlab(token, projectId, id)).await().indefinitely();

    if (response.getStatus().toWorkerStatus() == WorkerUnitStatus.IN_PROGRESS) {
      cache.invalidate(cacheKey).await().indefinitely();
    }

    return response;
  }

  private GitlabResponse fetchJobFromGitlab(
      SourceCodeToken token, SourceCodeProjectId projectId, WorkerUnitId workerUnitId) {
    var jobs = gitlabClient.getJobs(token.value(), projectId.value(), workerUnitId.value());
    if (jobs.size() > 1) {
      throw new MoreOneJobException(workerUnitId.value());
    }
    return jobs.getFirst();
  }
}
