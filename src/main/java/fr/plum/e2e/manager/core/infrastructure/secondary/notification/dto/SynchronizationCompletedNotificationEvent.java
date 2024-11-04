package fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
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
@JsonTypeName("SYNCHRONIZATION_COMPLETED_EVENT")
public class SynchronizationCompletedNotificationEvent extends AbstractNotificationEvent {

  private List<SynchronizationErrorResponse> syncErrors;
}
