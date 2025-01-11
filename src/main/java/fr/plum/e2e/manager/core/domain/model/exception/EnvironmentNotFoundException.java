package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentNotFoundException extends CustomException {
  public EnvironmentNotFoundException(EnvironmentId id) {
    super(
        Response.Status.NOT_FOUND,
        "environment-not-found",
        String.format("Environment with id '%s' not found.", id.value()));
  }
}
