package fr.plum.e2e.manager.sharedkernel.domain.exception;

import jakarta.ws.rs.core.Response;

public final class StringInvalidFormatException extends CustomException {

  private StringInvalidFormatException(StringInvalidFormatExceptionBuilder builder) {
    super(
        Response.Status.BAD_REQUEST,
        "string-invalid-format",
        "Value in "
            + builder.field
            + " was "
            + builder.value
            + " but format must match "
            + builder.regex);
  }

  public static StringInvalidFormatExceptionBuilder builder() {
    return new StringInvalidFormatExceptionBuilder();
  }

  public static class StringInvalidFormatExceptionBuilder {

    private String field;
    private String value;
    private String regex;

    public StringInvalidFormatExceptionBuilder field(String field) {
      this.field = field;
      return this;
    }

    public StringInvalidFormatExceptionBuilder value(String value) {
      this.value = value;
      return this;
    }

    public StringInvalidFormatExceptionBuilder regex(String regex) {
      this.regex = regex;
      return this;
    }

    public StringInvalidFormatException build() {
      return new StringInvalidFormatException(this);
    }
  }
}
