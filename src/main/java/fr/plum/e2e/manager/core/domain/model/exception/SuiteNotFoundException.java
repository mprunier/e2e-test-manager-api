package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class SuiteNotFoundException extends CustomException {
  public SuiteNotFoundException(SuiteConfigurationId id) {
    super(
        Response.Status.NOT_FOUND,
        "suite-not-found",
        String.format("Suite with id '%s' not found.", id.value()));
  }
}
