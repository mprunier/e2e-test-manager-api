package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationTestNotFoundException extends CustomException {

  public ConfigurationTestNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-test-not-found",
        String.format("Configuration Test with id %s not found.", id));
  }
}
