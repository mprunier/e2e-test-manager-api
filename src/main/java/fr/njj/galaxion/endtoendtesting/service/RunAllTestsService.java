package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunInProgressEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.AllTestsAlreadyRunningException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.service.environment.EnvironmentRetrievalService;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunAllTestsService {

    private final EnvironmentRetrievalService environmentRetrievalService;
    private final GitlabService gitlabService;
    private final PipelineService pipelineService;
    private final Event<AllTestsRunInProgressEvent> allTestsRunInProgressEvent;

    @Transactional
    public void runFromUser(Long environmentId, String createdBy) {
        pipelineService.assertNotConcurrentJobsReached();
        run(environmentId, createdBy);
    }

    @Transactional
    public void run(Long environmentId, String createdBy) {
        log.info("[{}] ran all tests on Environment id [{}].", createdBy, environmentId);
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

        pipelineService.create(environment, gitlabResponse.getId(), null);
        allTestsRunInProgressEvent.fire(AllTestsRunInProgressEvent.builder().environmentId(environmentId).build());
    }

    private static void assertSchedulerInProgress(EnvironmentEntity environment) {
        if (Boolean.TRUE.equals(environment.getIsRunningAllTests())) {
            throw new AllTestsAlreadyRunningException();
        }
    }
}

