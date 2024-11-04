package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationSynchronizationNotFoundException extends CustomException {

  public ConfigurationSynchronizationNotFoundException(Long id) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-synchronization-not-found",
        String.format("Last configuration synchronization with environment id %s not found.", id));
  }
}
