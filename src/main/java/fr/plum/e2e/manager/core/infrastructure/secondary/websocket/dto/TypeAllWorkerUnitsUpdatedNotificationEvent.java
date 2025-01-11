package fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@JsonTypeName("TYPE_ALL_WORKER_UNITS_UPDATED_EVENT")
public class TypeAllWorkerUnitsUpdatedNotificationEvent extends AbstractNotificationEvent {

  private List<WorkerUnitResponse> workerUnits;
}
