package fr.plum.e2e.manager.core.application.query.testresult;

import fr.plum.e2e.manager.core.domain.model.projection.TestResultErrorDetailsProjection;
import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailsQuery;
import fr.plum.e2e.manager.core.domain.port.out.view.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GetTestResultErrorDetailsQueryHandler
    implements QueryHandler<GetTestResultErrorDetailsQuery, TestResultErrorDetailsProjection> {

  private final GetTestResultPort getTestResultPort;

  public GetTestResultErrorDetailsQueryHandler(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  @Override
  public TestResultErrorDetailsProjection execute(GetTestResultErrorDetailsQuery query) {
    return getTestResultPort.findErrorDetail(query.testResultId());
  }
}
