package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.exception.AllTestsAlreadyRunningException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import io.quarkus.cache.CacheManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunSchedulerService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final GitlabService gitlabService;
    private final PipelineService pipelineService;
    private final CacheManager cacheManager;

    @Transactional
    public void runFromUser(Long environmentId, String createdBy) {
        pipelineService.assertNotConcurrentJobsReached();
        run(environmentId, createdBy);
    }

    @Transactional
    public void run(Long environmentId, String createdBy) {
        log.info("[{}] ran the Scheduler on Environment id [{}].", createdBy, environmentId);
        var environment = environmentRetrievalService.getEnvironment(environmentId);
        assertSchedulerInProgress(environment);
        environment.setIsRunningAllTests(true);

        var variablesBuilder = new StringBuilder();
        buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
        var gitlabResponse = gitlabService.runJob(environment.getBranch(),
                                                  environment.getToken(),
                                                  environment.getProjectId(),
                                                  null,
                                                  variablesBuilder.toString(),
                                                  null,
                                                  false);

        pipelineService.create(environment, PipelineType.ALL_TESTS, gitlabResponse.getId(), null);
        cacheManager.getCache("environment").ifPresent(cache -> cache.invalidate(environmentId).await().indefinitely());

    }

    private static void assertSchedulerInProgress(EnvironmentEntity environment) {
        if (environment.getIsRunningAllTests()) {
            throw new AllTestsAlreadyRunningException();
        }
    }
}

