package fr.plum.e2e.manager.core.domain.usecase.testresult;

import fr.plum.e2e.manager.core.domain.model.query.GetAllTestResultQuery;
import fr.plum.e2e.manager.core.domain.model.view.TestResultView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;
import java.util.List;

public class GetAllTestResultUseCase
    implements QueryUseCase<GetAllTestResultQuery, List<TestResultView>> {

  private final GetTestResultPort getTestResultPort;

  public GetAllTestResultUseCase(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  @Override
  public List<TestResultView> execute(GetAllTestResultQuery query) {
    return getTestResultPort.findAll(query.testConfigurationId());
  }
}
