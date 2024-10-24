package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.response.PipelineResponse;
import fr.njj.galaxion.endtoendtesting.service.PipelineGroupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveAllTestsPipelinesUseCase {

  private final PipelineGroupService pipelineGroupService;

  @Transactional
  public List<PipelineResponse> execute(long environmentId) {

    var pipelineGroup = pipelineGroupService.getLastPipelineGroup(environmentId);
    if (pipelineGroup != null) {
      return pipelineGroup.getPipelines().stream()
          .map(
              pipeline ->
                  PipelineResponse.builder()
                      .id(pipeline.getId())
                      .status(pipeline.getStatus())
                      .build())
          .toList();
    }

    return List.of();
  }
}
