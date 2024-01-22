package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ConfigurationSynchronizationException extends CustomException {

    public ConfigurationSynchronizationException(String description) {
        super(Response.Status.NOT_FOUND,
              "configuration-synchronization-error",
              "Error during synchronization. Contact administrator.",
              description);
    }
}
