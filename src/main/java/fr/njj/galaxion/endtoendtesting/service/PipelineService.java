package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConcurrentJobsReachedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineService {

    @Getter
    @ConfigProperty(name = "gitlab.job.max-in-parallel")
    Integer maxJobInParallel;

    private final PipelineRetrievalService pipelineRetrievalService;

    @Transactional
    public void cancel(String id) {
        pipelineRetrievalService.get(id).setStatus(PipelineStatus.CANCELED);
    }

    public void assertNotConcurrentJobsReached() {
        var testNumber = pipelineRetrievalService.countInProgress();
        if (testNumber >= maxJobInParallel) {
            throw new ConcurrentJobsReachedException();
        }
    }
}
