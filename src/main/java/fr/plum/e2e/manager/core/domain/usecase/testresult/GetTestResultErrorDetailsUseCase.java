package fr.plum.e2e.manager.core.domain.usecase.testresult;

import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailsQuery;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailsView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetTestResultErrorDetailsUseCase
    implements QueryUseCase<GetTestResultErrorDetailsQuery, TestResultErrorDetailsView> {

  private final GetTestResultPort getTestResultPort;

  public GetTestResultErrorDetailsUseCase(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  @Override
  public TestResultErrorDetailsView execute(GetTestResultErrorDetailsQuery query) {
    return getTestResultPort.findErrorDetail(query.testResultId());
  }
}
