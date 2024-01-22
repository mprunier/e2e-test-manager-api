package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class EnvironmentBranchAlreadyExistException extends CustomException {

    public EnvironmentBranchAlreadyExistException(String path) {
        super(Response.Status.BAD_REQUEST,
              "environment-branch-already-exist",
              String.format("Environment with branch name [%s] already exist.", path));
    }
}
