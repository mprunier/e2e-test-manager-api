package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationTestNotFoundException extends CustomException {

  public ConfigurationTestNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-test-not-found",
        String.format("Configuration Test with id %s not found.", id));
  }
}
