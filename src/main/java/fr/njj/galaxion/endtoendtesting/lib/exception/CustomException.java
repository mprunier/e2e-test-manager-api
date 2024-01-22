package fr.njj.galaxion.endtoendtesting.lib.exception;

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

    public CustomException(Response.Status status, String title, String detail) {
        this.status = status.getStatusCode();
        this.title = title;
        this.detail = detail;
    }

    public CustomException(Response.Status status,
                           String title,
                           String detail,
                           String description) {
        this.status = status.getStatusCode();
        this.title = title;
        this.detail = detail;
        this.description = description;
    }

    @Override
    public String toString() {
        return StringUtils.isNotBlank(description) ?
                String.format("%s (%s) %s : %s", getStatus(), getTitle(), getDetail(), getDescription()) :
                String.format("%s (%s) %s", getStatus(), getTitle(), getDetail());
    }
}
