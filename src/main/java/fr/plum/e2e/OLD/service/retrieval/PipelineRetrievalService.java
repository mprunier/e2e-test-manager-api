package fr.plum.e2e.OLD.service.retrieval;

import fr.plum.e2e.OLD.domain.enumeration.PipelineType;
import fr.plum.e2e.OLD.domain.exception.JobNotFoundException;
import fr.plum.e2e.OLD.domain.internal.InProgressPipelinesInternal;
import fr.plum.e2e.OLD.domain.internal.PipelineDetailsInternal;
import fr.plum.e2e.OLD.model.entity.PipelineEntity;
import fr.plum.e2e.OLD.model.entity.PipelineGroupEntity;
import fr.plum.e2e.OLD.model.repository.PipelineRepository;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
        //        var configurationTestIds =
        //            pipeline.getConfigurationTestIdsFilter().stream()
        //                .map(Long::valueOf)
        //                .collect(Collectors.toSet());
        var configurationTests = configurationTestRetrievalService.getAllByIds(Set.of());
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
