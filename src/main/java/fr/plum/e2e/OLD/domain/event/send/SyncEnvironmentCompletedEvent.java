package fr.plum.e2e.OLD.domain.event.send;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.OLD.domain.event.AbstractEvent;
import fr.plum.e2e.OLD.domain.response.EnvironmentResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.SynchronizationErrorResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@JsonTypeName("SYNC_ENVIRONMENT_COMPLETED_EVENT")
public class SyncEnvironmentCompletedEvent extends AbstractEvent {

  private EnvironmentResponse environment;
  private List<SynchronizationErrorResponse> syncErrors;
}
