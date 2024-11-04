package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SynchronizationCommonException extends CustomException {

  public SynchronizationCommonException(String description) {
    super(
        Response.Status.NOT_FOUND,
        "configuration-synchronization-error",
        "Error during synchronization. Contact administrator. (" + description + ")",
        description);
  }
}
