package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.NO_SUITE;

public class SuiteNoTitleException extends CustomException {

    public SuiteNoTitleException() {
        super(Response.Status.BAD_REQUEST,
              "suite-no-title",
              String.format("Suite with title '%s' is forbidden.", NO_SUITE));
    }
}
