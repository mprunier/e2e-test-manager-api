package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerRunningException extends CustomException {

  public SchedulerRunningException() {
    super(
        Response.Status.BAD_REQUEST,
        "scheduler-running",
        "Scheduler is already running. Please wait a few minutes.");
  }
}
