package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.exception.JobNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressTestInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.PipelineRepository;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineRetrievalService {

  private final PipelineRepository pipelineRepository;
  private final TestRetrievalService testRetrievalService;

  @Transactional
  public List<PipelineEntity> getOldInProgress(Integer oldMinutes) {
    return pipelineRepository.getOldInProgress(oldMinutes);
  }

  @Transactional
  public PipelineEntity get(String id) {
    return pipelineRepository.findByIdOptional(id).orElseThrow(() -> new JobNotFoundException(id));
  }

  @Transactional
  public EnvironmentEntity getEnvironment(String id) {
    return get(id).getEnvironment();
  }

  @Transactional
  public long countInProgress() {
    return pipelineRepository.countInProgress();
  }

  @Transactional
  public boolean isAllTestRunning(long environmentId) {
    return pipelineRepository.isAllTestRunning(environmentId);
  }

  @CacheResult(cacheName = "in_progress_pipelines")
  @Transactional
  public InProgressTestInternal getAllInProgressTests(long environmentId) {
    var pipelines = pipelineRepository.getAllInProgress(environmentId);
    boolean isAllTestsInProgress = false;
    var numberOfTestInProgressById = new HashMap<Long, Integer>();

    for (PipelineEntity pipeline : pipelines) {
      if (PipelineType.ALL.equals(pipeline.getType())
          || PipelineType.ALL_IN_PARALLEL.equals(pipeline.getType())) {
        isAllTestsInProgress = true;
      } else {
        var testIds = pipeline.getTestIds().stream().map(Long::valueOf).toList();
        var tests = testRetrievalService.getAll(testIds);
        tests.forEach(
            test -> {
              long configurationTestId = test.getConfigurationTest().getId();
              numberOfTestInProgressById.put(
                  configurationTestId,
                  numberOfTestInProgressById.getOrDefault(configurationTestId, 0) + 1);
            });
      }
    }

    return InProgressTestInternal.builder()
        .numberOfTestInProgressById(numberOfTestInProgressById)
        .isAllTestsInProgress(isAllTestsInProgress)
        .build();
  }
}
