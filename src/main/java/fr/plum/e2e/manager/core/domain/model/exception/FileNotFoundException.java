package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class FileNotFoundException extends CustomException {
  public FileNotFoundException(TestConfigurationId id) {
    super(
        Response.Status.NOT_FOUND,
        "file-testFilter-not-found",
        String.format("File with testFilter id '%s' not found.", id.value()));
  }

  public FileNotFoundException(SuiteConfigurationId id) {
    super(
        Response.Status.NOT_FOUND,
        "file-suiteFilter-not-found",
        String.format("File with suiteFilter id '%s' not found.", id.value()));
  }
}
