package fr.plum.e2e.OLD.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.OLD.domain.event.AbstractEvent;
import fr.plum.e2e.OLD.domain.response.PipelineResponse;
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
