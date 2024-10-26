package fr.njj.galaxion.endtoendtesting.service.retrieval;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import fr.njj.galaxion.endtoendtesting.domain.exception.JobNotFoundException;
import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressPipelinesInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.PipelineDetailsInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineGroupEntity;
import fr.njj.galaxion.endtoendtesting.model.repository.PipelineRepository;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class PipelineRetrievalService {

  private final PipelineRepository pipelineRepository;
  private final ConfigurationTestRetrievalService configurationTestRetrievalService;

  @Transactional
  public List<PipelineEntity> getOldInProgress(Integer oldMinutes) {
    return pipelineRepository.getOldInProgress(oldMinutes);
  }

  @Transactional
  public PipelineEntity get(String id) {
    return pipelineRepository.findByIdOptional(id).orElseThrow(() -> new JobNotFoundException(id));
  }

  @Transactional
  public PipelineGroupEntity getGroup(String id) {
    return get(id).getPipelineGroup();
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
  public InProgressPipelinesInternal getInProgressPipelines(@CacheKey long environmentId) {
    var pipelines = pipelineRepository.getAllInProgress(environmentId);
    boolean isAllTests = false;
    var pipelinesByConfigurationTestId = new HashMap<Long, List<PipelineDetailsInternal>>();

    for (PipelineEntity pipeline : pipelines) {
      if (PipelineType.ALL.equals(pipeline.getType())
          || PipelineType.ALL_IN_PARALLEL.equals(pipeline.getType())) {
        isAllTests = true;
      } else {
        var configurationTestIds =
            pipeline.getConfigurationTestIdsFilter().stream().map(Long::valueOf).toList();
        var configurationTests =
            configurationTestRetrievalService.getAllByIds(configurationTestIds);
        configurationTests.forEach(
            configurationTest -> {
              var pipelineIds =
                  new ArrayList<>(
                      pipelinesByConfigurationTestId.getOrDefault(
                          configurationTest.getId(), new ArrayList<>()));
              pipelineIds.add(
                  PipelineDetailsInternal.builder()
                      .id(pipeline.getId())
                      .createdAt(pipeline.getCreatedAt())
                      .createdBy(pipeline.getCreatedBy())
                      .build());
              pipelinesByConfigurationTestId.put(configurationTest.getId(), pipelineIds);
            });
      }
    }

    return InProgressPipelinesInternal.builder()
        .pipelinesByConfigurationTestId(pipelinesByConfigurationTestId)
        .isAllTests(isAllTests)
        .build();
  }
}
