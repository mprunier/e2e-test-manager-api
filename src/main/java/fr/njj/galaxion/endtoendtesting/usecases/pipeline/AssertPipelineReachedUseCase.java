package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.service.AssertPipelineReachedService;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class AssertPipelineReachedUseCase {

  private final AssertPipelineReachedService assertPipelineReachedService;

  public void execute() {
    assertPipelineReachedService.assertPipeline();
  }
}
