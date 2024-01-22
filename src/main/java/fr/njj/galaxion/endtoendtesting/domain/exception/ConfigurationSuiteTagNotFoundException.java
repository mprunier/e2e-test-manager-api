package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationSuiteTagNotFoundException extends CustomException {

  public ConfigurationSuiteTagNotFoundException(String tag) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-suite-tag-not-found",
        String.format("Configuration Suite with tag %s not found.", tag));
  }
}
