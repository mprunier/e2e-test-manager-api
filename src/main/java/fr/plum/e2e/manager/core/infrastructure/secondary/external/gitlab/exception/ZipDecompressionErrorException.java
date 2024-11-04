package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ZipDecompressionErrorException extends CustomException {

  public ZipDecompressionErrorException() {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "zip-decompression-error",
        "Zip decompression error during artifact extraction.");
  }
}
