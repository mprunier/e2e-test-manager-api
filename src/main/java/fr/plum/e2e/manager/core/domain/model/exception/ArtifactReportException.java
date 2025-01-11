package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class ArtifactReportException extends CustomException {
  public ArtifactReportException() {
    super(
        Response.Status.INTERNAL_SERVER_ERROR,
        "artifact-report-error",
        "Error while extracting the report artifacts");
  }
}
