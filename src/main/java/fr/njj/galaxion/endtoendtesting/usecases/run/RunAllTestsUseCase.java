package fr.njj.galaxion.endtoendtesting.usecases.run;

import static fr.njj.galaxion.endtoendtesting.helper.EnvironmentHelper.buildVariablesEnvironment;

import fr.njj.galaxion.endtoendtesting.client.gitlab.response.GitlabResponse;
import fr.njj.galaxion.endtoendtesting.domain.event.AllTestsRunInProgressEvent;
import fr.njj.galaxion.endtoendtesting.domain.exception.AllTestsAlreadyRunningException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.gitlab.RunGitlabJobService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.EnvironmentRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunAllTestsUseCase {

  private final EnvironmentRetrievalService environmentRetrievalService;
  private final RunGitlabJobService runGitlabJobService;

  private final Event<AllTestsRunInProgressEvent> allTestsRunInProgressEvent;

  @Transactional
  public void execute(Long environmentId, String createdBy) {
    log.info("[{}] ran all tests on Environment id [{}].", createdBy, environmentId);
    var environment = environmentRetrievalService.get(environmentId);
    assertSchedulerInProgress(environment);
    environment.setIsRunningAllTests(true);

    var variablesBuilder = new StringBuilder();
    buildVariablesEnvironment(environment.getVariables(), variablesBuilder);
    var gitlabResponse =
        runGitlabJobService.runJob(
            environment.getBranch(),
            environment.getToken(),
            environment.getProjectId(),
            null,
            variablesBuilder.toString(),
            null,
            null,
            false);

    createPipeline(gitlabResponse, environment);

    allTestsRunInProgressEvent.fire(
        AllTestsRunInProgressEvent.builder().environmentId(environmentId).build());
  }

  private static void createPipeline(GitlabResponse gitlabResponse, EnvironmentEntity environment) {
    PipelineEntity.builder().id(gitlabResponse.getId()).environment(environment).build().persist();
  }

  private static void assertSchedulerInProgress(EnvironmentEntity environment) {
    if (Boolean.TRUE.equals(environment.getIsRunningAllTests())) {
      throw new AllTestsAlreadyRunningException();
    }
  }
}
