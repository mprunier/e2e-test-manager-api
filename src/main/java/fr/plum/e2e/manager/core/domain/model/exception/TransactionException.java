package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TransactionException extends CustomException {
  public TransactionException(Exception exception) {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "transaction-sql-error",
        "An error occurred. Please contact the administrator.",
        exception);
  }
}
