package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;
import jakarta.validation.constraints.NotBlank;

public record RunVariableRequest(@NotBlank String name, @NotBlank String value) {

  public WorkerVariable toCommand() {
    return new WorkerVariable(name, value);
  }
}
