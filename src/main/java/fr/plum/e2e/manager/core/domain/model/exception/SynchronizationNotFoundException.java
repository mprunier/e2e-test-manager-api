package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SynchronizationNotFoundException extends CustomException {
  public SynchronizationNotFoundException(EnvironmentId id) {
    super(
        Response.Status.NOT_FOUND,
        "synchronization-not-found",
        String.format("Synchronization process with environment id '%s' not found.", id.value()));
  }
}
