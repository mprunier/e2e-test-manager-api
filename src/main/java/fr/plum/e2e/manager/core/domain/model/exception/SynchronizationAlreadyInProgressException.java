package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SynchronizationAlreadyInProgressException extends CustomException {
  public SynchronizationAlreadyInProgressException(EnvironmentId environmentId) {
    super(
        Response.Status.BAD_REQUEST,
        "synchronization-already-in-progress",
        String.format(
            "Environment with id '%s' already in synchronization.", environmentId.value()));
  }
}
