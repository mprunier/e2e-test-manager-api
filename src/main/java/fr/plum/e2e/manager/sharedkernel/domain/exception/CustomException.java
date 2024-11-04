package fr.plum.e2e.manager.sharedkernel.domain.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class CustomException extends RuntimeException {

  private int status;
  private String title;
  private String detail;
  private String description;

  public CustomException(Throwable cause) {
    super(cause);
  }

  public CustomException(String detail) {
    this.detail = detail;
  }

  public CustomException(Response.Status status, String title, String detail) {
    this.status = status.getStatusCode();
    this.title = title;
    this.detail = detail;
  }

  public CustomException(Response.Status status, String title, String detail, String description) {
    this.status = status.getStatusCode();
    this.title = title;
    this.detail = detail;
    this.description = description;
  }

  public CustomException(Response.Status status, String title, String detail, Throwable cause) {
    super(cause);
    this.status = status.getStatusCode();
    this.title = title;
    this.detail = detail;
  }

  @Override
  public String toString() {
    return StringUtils.isNotBlank(description)
        ? String.format("%s (%s) %s : %s", getStatus(), getTitle(), getDetail(), getDescription())
        : String.format("%s (%s) %s", getStatus(), getTitle(), getDetail());
  }
}
