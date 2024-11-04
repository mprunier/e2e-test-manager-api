package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerPipelineIdNotFoundException extends CustomException {

  public SchedulerPipelineIdNotFoundException(String id) {
    super(
        Response.Status.NOT_FOUND,
        "scheduler-worker-not-found",
        String.format("Scheduler with worker id %s not found.", id));
  }
}
