package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkerUnitResponse(
    String id, WorkerUnitStatus status, String statusDescription, List<String> fileNames) {

  public static WorkerUnitResponse from(WorkerUnit workerUnit) {
    return builder()
        .id(workerUnit.getId().value())
        .status(workerUnit.getStatus())
        .statusDescription(
            workerUnit.getStatus().getErrorMessage() != null
                ? workerUnit.getStatus().getErrorMessage()
                : workerUnit.getStatus().name())
        .fileNames(
            workerUnit.getFilter() != null && workerUnit.getFilter().fileNames() != null
                ? workerUnit.getFilter().fileNames().stream().map(FileName::value).toList()
                : List.of())
        .build();
  }

  public static List<WorkerUnitResponse> fromWorkers(List<WorkerUnit> workerUnits) {
    return workerUnits.stream().map(WorkerUnitResponse::from).toList();
  }
}
