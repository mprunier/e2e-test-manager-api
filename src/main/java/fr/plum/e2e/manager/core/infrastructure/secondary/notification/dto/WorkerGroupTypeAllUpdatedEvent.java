package fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonTypeName("WORKER_GROUP_TYPE_ALL_UPDATED_EVENT")
public class WorkerGroupTypeAllUpdatedEvent extends AbstractNotificationEvent {

  private List<WorkerUnitResponse> workers;
}
