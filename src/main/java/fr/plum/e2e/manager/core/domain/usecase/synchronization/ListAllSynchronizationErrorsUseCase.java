package fr.plum.e2e.manager.core.domain.usecase.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.out.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.domain.port.in.QueryUseCase;
import java.util.List;

public class ListAllSynchronizationErrorsUseCase
    implements QueryUseCase<CommonQuery, List<SynchronizationError>> {

  private final SynchronizationService synchronizationService;

  public ListAllSynchronizationErrorsUseCase(
      SynchronizationRepositoryPort synchronizationRepositoryPort) {
    this.synchronizationService = new SynchronizationService(synchronizationRepositoryPort);
  }

  @Override
  public List<SynchronizationError> execute(CommonQuery query) {
    var synchronization = synchronizationService.getSynchronization(query.environmentId());
    return synchronization.getErrors();
  }
}