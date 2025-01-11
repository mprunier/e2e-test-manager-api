package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class BuildFileConfigurationException extends CustomException {
  public BuildFileConfigurationException(String details, String lineSource) {
    super(
        Response.Status.BAD_REQUEST,
        "synchronization-error",
        String.format("Error : %s on line : %s.", details, lineSource));
  }
}
