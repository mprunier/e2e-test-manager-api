package fr.plum.e2e.OLD.domain.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentAlreadyInSyncProgressException extends CustomException {

  public EnvironmentAlreadyInSyncProgressException() {
    super(
        Response.Status.BAD_REQUEST,
        "environment-configuration-already-in-sync",
        "Environment configuration already in synchronization. Please wait.");
  }
}
