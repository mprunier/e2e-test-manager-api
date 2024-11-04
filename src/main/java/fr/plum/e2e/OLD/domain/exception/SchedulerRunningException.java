package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerRunningException extends CustomException {

  public SchedulerRunningException() {
    super(
        Response.Status.BAD_REQUEST,
        "scheduler-running",
        "Scheduler is already running. Please wait a few minutes.");
  }
}
