package fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.request;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerVariable;

public record RunVariableRequest(String name, String value) {

  public WorkerVariable toCommand() {
    return new WorkerVariable(name, value);
  }
}
