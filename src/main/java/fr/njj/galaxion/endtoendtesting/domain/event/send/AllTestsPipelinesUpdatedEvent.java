package fr.njj.galaxion.endtoendtesting.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.njj.galaxion.endtoendtesting.domain.event.AbstractEvent;
import fr.njj.galaxion.endtoendtesting.domain.response.PipelineResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("ALL_TESTS_PIPELINES_UPDATED")
public class AllTestsPipelinesUpdatedEvent extends AbstractEvent {

  private List<PipelineResponse> pipelines;
}
