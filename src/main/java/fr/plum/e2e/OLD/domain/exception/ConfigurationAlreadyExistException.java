package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationAlreadyExistException extends CustomException {

  public ConfigurationAlreadyExistException(String path) {
    super(
        Response.Status.BAD_REQUEST,
        "configuration-already-exist",
        String.format("Configuration with path %s already exist.", path));
  }
}
