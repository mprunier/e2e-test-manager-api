package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerNotFoundException extends CustomException {

  public SchedulerNotFoundException(EnvironmentId id) {
    super(
        Response.Status.NOT_FOUND,
        "scheduler-not-found",
        String.format("Scheduler with environment id %s not found.", id.value()));
  }
}
