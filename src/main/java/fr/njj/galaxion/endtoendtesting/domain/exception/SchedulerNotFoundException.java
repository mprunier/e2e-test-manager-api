package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SchedulerNotFoundException extends CustomException {

    public SchedulerNotFoundException(Long id) {
        super(Response.Status.NOT_FOUND,
              "scheduler-not-found",
              String.format("Scheduler with id %s not found.", id));
    }
}
