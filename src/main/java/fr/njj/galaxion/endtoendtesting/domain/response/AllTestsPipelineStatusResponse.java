package fr.njj.galaxion.endtoendtesting.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AllTestsPipelineStatusResponse {

  private int cancelPipelines;

  private int finishedPipelines;

  private int inProgressPipelines;

  private int totalPipelines;
}
