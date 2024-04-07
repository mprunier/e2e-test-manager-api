package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConcurrentJobsReachedException;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
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
public class PipelineService {

    @Getter
    @ConfigProperty(name = "gitlab.job.max-in-parallel")
    Integer maxJobInParallel;

    private final PipelineRetrievalService pipelineRetrievalService;

    @Transactional
    public void create(EnvironmentEntity environment, PipelineType type, String pipelineId, List<String> testIds) {
        PipelineEntity.builder()
                      .id(pipelineId)
                      .environment(environment)
                      .type(type)
                      .testIds(testIds)
                      .build()
                      .persist();
    }

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
