package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerPipelineIdNotFoundException extends CustomException {

  public SchedulerPipelineIdNotFoundException(String id) {
    super(
        Response.Status.NOT_FOUND,
        "scheduler-pipeline-not-found",
        String.format("Scheduler with pipeline id %s not found.", id));
  }
}
