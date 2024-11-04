package fr.plum.e2e.manager.core.domain.model.exception;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.core.Response;

public class TestNotFoundException extends CustomException {
  public TestNotFoundException(TestConfigurationId id) {
    super(
        Response.Status.NOT_FOUND,
        "test-not-found",
        String.format("Test with id '%s' not found.", id.value()));
  }
}
