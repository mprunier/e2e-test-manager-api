package fr.njj.galaxion.endtoendtesting.usecases.pipeline;

import fr.njj.galaxion.endtoendtesting.domain.response.AllTestsPipelineStatusResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.PipelineEntity;
import fr.njj.galaxion.endtoendtesting.service.PipelineGroupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveAllTestsPipelineStatusUseCase {

  private final PipelineGroupService pipelineGroupService;

  @Transactional
  public AllTestsPipelineStatusResponse execute(long environmentId) {

    var pipelineGroup = pipelineGroupService.getLastPipelineGroup(environmentId);
    if (pipelineGroup != null) {

      int cancelPipelines = 0;
      int finishedPipelines = 0;
      int inProgressPipelines = 0;

      for (PipelineEntity pipeline : pipelineGroup.getPipelines()) {
        switch (pipeline.getStatus()) {
          case CANCELED:
            cancelPipelines++;
            break;
          case FINISH:
            finishedPipelines++;
            break;
          case IN_PROGRESS:
            inProgressPipelines++;
            break;
          default:
            break;
        }
      }

      return AllTestsPipelineStatusResponse.builder()
          .cancelPipelines(cancelPipelines)
          .finishedPipelines(finishedPipelines)
          .inProgressPipelines(inProgressPipelines)
          .totalPipelines(pipelineGroup.getTotalPipelines())
          .build();
    }

    return AllTestsPipelineStatusResponse.builder().build();
  }
}
