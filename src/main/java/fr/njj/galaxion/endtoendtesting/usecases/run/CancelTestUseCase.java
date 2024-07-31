package fr.njj.galaxion.endtoendtesting.usecases.run;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.event.TestRunCompletedEvent;
import fr.njj.galaxion.endtoendtesting.service.gitlab.CancelGitlabPipelineService;
import fr.njj.galaxion.endtoendtesting.service.retrieval.TestRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static fr.njj.galaxion.endtoendtesting.helper.TestHelper.updateStatus;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelTestUseCase {

    private final TestRetrievalService testRetrievalService;
    private final CancelGitlabPipelineService cancelGitlabPipelineService;

    private final Event<TestRunCompletedEvent> testRunCompletedEvent;

    @Transactional
    public void execute(
            String pipelineId,
            List<String> testStrIds) {
        var testIds = testStrIds.stream().map(Long::valueOf).toList();

        var tests = testRetrievalService.getAll(testIds);
        var environment = tests.getFirst().getConfigurationTest().getEnvironment();
        cancelGitlabPipelineService.cancelPipeline(environment.getToken(), environment.getProjectId(), pipelineId);
        updateStatus(tests, ConfigurationStatus.CANCELED);
        testRunCompletedEvent.fire(TestRunCompletedEvent.builder().environmentId(environment.getId()).build());
    }
}

