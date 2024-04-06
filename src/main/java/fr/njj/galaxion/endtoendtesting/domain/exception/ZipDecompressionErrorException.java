package fr.njj.galaxion.endtoendtesting.domain.exception;

import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ZipDecompressionErrorException extends CustomException {

    public ZipDecompressionErrorException() {
        super(Response.Status.INTERNAL_SERVER_ERROR,
              "zip-decompression-error",
              "Zip decompression error.");
    }
}
