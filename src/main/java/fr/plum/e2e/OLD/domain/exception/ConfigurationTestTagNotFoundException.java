package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationTestTagNotFoundException extends CustomException {

  public ConfigurationTestTagNotFoundException(String tag) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-test-tag-not-found",
        String.format("Configuration Test with tag %s not found.", tag));
  }
}
