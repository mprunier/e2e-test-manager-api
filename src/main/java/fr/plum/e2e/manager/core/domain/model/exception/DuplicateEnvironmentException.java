package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class DuplicateEnvironmentException extends CustomException {
  public DuplicateEnvironmentException(EnvironmentDescription description) {
    super(
        Response.Status.CONFLICT,
        "duplicate-environment",
        String.format("Environment with description '%s' already exists.", description.value()));
  }
}
