package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnit;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerUnitStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Builder
public record WorkerUnitResponse(
    @NotBlank String id,
    @NotNull WorkerUnitStatus status,
    String statusDescription,
    List<String> fileNames) {

  public static WorkerUnitResponse from(WorkerUnit workerUnit) {
    return builder()
        .id(workerUnit.getId().value())
        .status(workerUnit.getStatus())
        .statusDescription(workerUnit.getStatus().getErrorMessage())
        .fileNames(
            workerUnit.getFilter() != null && workerUnit.getFilter().fileNames() != null
                ? workerUnit.getFilter().fileNames().stream().map(FileName::value).toList()
                : new ArrayList<>())
        .build();
  }

  public static List<WorkerUnitResponse> fromWorkers(List<WorkerUnit> workerUnits) {
    return workerUnits.stream().map(WorkerUnitResponse::from).toList();
  }
}
