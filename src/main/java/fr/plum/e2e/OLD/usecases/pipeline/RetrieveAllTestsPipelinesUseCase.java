package fr.plum.e2e.OLD.usecases.pipeline;

import fr.plum.e2e.OLD.domain.response.PipelineResponse;
import fr.plum.e2e.OLD.service.PipelineGroupService;
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
                      .statusDescription(pipeline.getStatus().getErrorMessage())
                      //                      .filesFilter(pipeline.getFilesFilter())
                      .build())
          .toList();
    }

    return List.of();
  }
}
