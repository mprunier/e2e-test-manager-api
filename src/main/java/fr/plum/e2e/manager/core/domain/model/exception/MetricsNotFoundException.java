package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class MetricsNotFoundException extends CustomException {
  public MetricsNotFoundException(EnvironmentId id) {
    super(
        Response.Status.NOT_FOUND,
        "metrics-not-found",
        String.format("Metric not found on environment with id '%s'.", id.value()));
  }
}