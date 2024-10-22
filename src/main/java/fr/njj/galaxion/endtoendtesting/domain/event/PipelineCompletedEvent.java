package fr.njj.galaxion.endtoendtesting.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineStatus;
import fr.njj.galaxion.endtoendtesting.domain.enumeration.PipelineType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("PIPELINE_COMPLETED_EVENT")
public class PipelineCompletedEvent extends AbstractEvent {

  private String pipelineId;

  private PipelineType type;

  private PipelineStatus status;

  private List<String> configurationTestIdsFilter;
}
