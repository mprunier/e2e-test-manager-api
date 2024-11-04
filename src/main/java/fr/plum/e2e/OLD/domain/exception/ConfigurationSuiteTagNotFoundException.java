package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationSuiteTagNotFoundException extends CustomException {

  public ConfigurationSuiteTagNotFoundException(String tag) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-configuration-tag-not-found",
        String.format("Configuration Suite with tag %s not found.", tag));
  }
}
