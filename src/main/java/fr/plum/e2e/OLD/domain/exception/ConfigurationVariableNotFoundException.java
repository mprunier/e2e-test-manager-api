package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationVariableNotFoundException extends CustomException {

  public ConfigurationVariableNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-variable-not-found",
        String.format("Configuration Variable ID %s not found.", id));
  }
}
