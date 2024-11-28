package fr.plum.e2e.manager.core.domain.usecase.testresult;

import fr.plum.e2e.manager.core.domain.model.query.GetTestResultErrorDetailQuery;
import fr.plum.e2e.manager.core.domain.model.view.TestResultErrorDetailView;
import fr.plum.e2e.manager.core.domain.port.out.query.GetTestResultPort;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;

public class GetTestResultErrorDetailUseCase
    implements QueryUseCase<GetTestResultErrorDetailQuery, TestResultErrorDetailView> {

  private final GetTestResultPort getTestResultPort;

  public GetTestResultErrorDetailUseCase(GetTestResultPort getTestResultPort) {
    this.getTestResultPort = getTestResultPort;
  }

  @Override
  public TestResultErrorDetailView execute(GetTestResultErrorDetailQuery query) {
    return getTestResultPort.findErrorDetail(query.testResultId());
  }
}
