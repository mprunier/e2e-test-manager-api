package fr.plum.e2e.manager.core.application.query.testresult;

import fr.plum.e2e.manager.core.domain.model.projection.TestResultProjection;
import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.port.out.view.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GetAllTestResultQueryHandler
    implements QueryHandler<GetAllTestResultQuery, List<TestResultProjection>> {

  private final GetTestResultPort getTestResultPort;

  public GetAllTestResultQueryHandler(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  @Override
  public List<TestResultProjection> execute(GetAllTestResultQuery query) {
    return getTestResultPort.findAll(query.testConfigurationId());
  }
}
