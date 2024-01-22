package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentAlreadyInSyncProgressException extends CustomException {

    public EnvironmentAlreadyInSyncProgressException() {
        super(Response.Status.BAD_REQUEST,
              "environment-configuration-already-in-sync",
              "Environment configuration already in synchronization. Please wait.");
    }
}
