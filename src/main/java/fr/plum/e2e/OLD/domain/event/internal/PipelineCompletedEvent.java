package fr.plum.e2e.OLD.domain.event.internal;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.OLD.domain.enumeration.PipelineType;
import fr.plum.e2e.OLD.domain.event.AbstractEvent;
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

  private List<String> configurationTestIdsFilter;
}
