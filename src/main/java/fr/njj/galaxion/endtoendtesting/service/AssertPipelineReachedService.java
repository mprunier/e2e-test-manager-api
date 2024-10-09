package fr.njj.galaxion.endtoendtesting.service;

import fr.njj.galaxion.endtoendtesting.domain.exception.ConcurrentJobsReachedException;
import fr.njj.galaxion.endtoendtesting.service.retrieval.PipelineRetrievalService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AssertPipelineReachedService {

  @Getter
  @ConfigProperty(name = "gitlab.job.max-in-parallel")
  Integer maxJobInParallel;

  private final PipelineRetrievalService pipelineRetrievalService;

  public void assertPipeline() {
    var testNumber = pipelineRetrievalService.countInProgress();
    if (testNumber >= maxJobInParallel) {
      throw new ConcurrentJobsReachedException();
    }
  }
}
