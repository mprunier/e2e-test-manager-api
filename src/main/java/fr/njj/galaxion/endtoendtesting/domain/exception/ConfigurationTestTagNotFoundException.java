package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationTestTagNotFoundException extends CustomException {

  public ConfigurationTestTagNotFoundException(String tag) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-test-tag-not-found",
        String.format("Configuration Test with tag %s not found.", tag));
  }
}
