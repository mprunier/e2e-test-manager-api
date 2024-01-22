package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.JobType;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConcurrentJobsReachedException;
import fr.njj.galaxion.endtoendtesting.model.entity.JobEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class JobService {

    @Getter
    @ConfigProperty(name = "gitlab.job.max-in-parallel")
    Integer maxJobInParallel;

    private final JobRetrievalService jobRetrievalService;

    @Transactional
    public void create(JobType type, String pipelineId, List<String> testIds) {
        JobEntity.builder()
                 .type(type)
                 .pipelineId(pipelineId)
                 .testIds(testIds)
                 .build()
                 .persist();
    }

    @Transactional
    public void finish(long id) {
        jobRetrievalService.get(id).setStatus(JobStatus.FINISH);
    }

    @Transactional
    public void cancel(long id) {
        jobRetrievalService.get(id).setStatus(JobStatus.CANCELED);
    }

    public void assertNotConcurrentJobsReached() {
        var testNumber = jobRetrievalService.countInProgress();
        if (testNumber >= maxJobInParallel) {
            throw new ConcurrentJobsReachedException();
        }
    }
}
