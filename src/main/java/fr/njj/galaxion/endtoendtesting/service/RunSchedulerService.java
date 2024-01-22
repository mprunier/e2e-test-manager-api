package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobType;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
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
    private final SchedulerRetrievalService schedulerRetrievalService;
    private final GitlabService gitlabService;
    private final JobService jobService;
    private final SchedulerService schedulerService;

    @Transactional
    public void runFromUser(Long environmentId, String createdBy) {
        jobService.assertNotConcurrentJobsReached();
        run(environmentId, createdBy);
    }

    @Transactional
    public void run(Long environmentId, String createdBy) {
        log.info("[{}] ran the Scheduler on Environment id [{}].", createdBy, environmentId);
        schedulerRetrievalService.assertExistInProgressByEnvironment(environmentId);

        var environment = environmentRetrievalService.getEnvironment(environmentId);

        var variablesBuilder = new StringBuilder();
        buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
        var gitlabResponse = gitlabService.runJob(environment.getBranch(),
                                                  environment.getToken(),
                                                  environment.getProjectId(),
                                                  null,
                                                  variablesBuilder.toString(),
                                                  null,
                                                  false);

        schedulerService.create(environment, createdBy, gitlabResponse.getId());
        jobService.create(JobType.ALL_TESTS, gitlabResponse.getId(), null);
    }
}

