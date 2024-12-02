package fr.plum.e2e.manager.sharedkernel.domain.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class CustomException extends RuntimeException {

  private int status;
  private String title;
  private String description;
  private String details;

  public CustomException(Throwable cause) {
    super(cause);
  }

  public CustomException(String description) {
    this.description = description;
  }

  public CustomException(Response.Status status, String title, String description) {
    this.status = status.getStatusCode();
    this.title = title;
    this.description = description;
  }

  public CustomException(Response.Status status, String title, String description, String details) {
    this.status = status.getStatusCode();
    this.title = title;
    this.description = description;
    this.details = details;
  }

  public CustomException(
      Response.Status status, String title, String description, Throwable cause) {
    super(cause);
    this.status = status.getStatusCode();
    this.title = title;
    this.description = description;
  }

  @Override
  public String toString() {
    return StringUtils.isNotBlank(details)
        ? String.format("%s (%s) %s : %s", getStatus(), getTitle(), getDetails(), getDescription())
        : String.format("%s (%s) %s", getStatus(), getTitle(), getDetails());
  }
}
