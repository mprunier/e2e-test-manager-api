package fr.plum.e2e.manager.core.application.query.synchronization;

import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.port.repository.SynchronizationRepositoryPort;
import fr.plum.e2e.manager.core.domain.service.SynchronizationService;
import fr.plum.e2e.manager.sharedkernel.application.query.QueryHandler;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ListAllSynchronizationErrorsQueryHandler
    implements QueryHandler<CommonQuery, List<SynchronizationError>> {

  private final SynchronizationService synchronizationService;

  public ListAllSynchronizationErrorsQueryHandler(
      SynchronizationRepositoryPort synchronizationRepositoryPort) {
    this.synchronizationService = new SynchronizationService(synchronizationRepositoryPort);
  }

  @Override
  public List<SynchronizationError> execute(CommonQuery query) {
    var synchronization = synchronizationService.getSynchronization(query.environmentId());
    return synchronization.getErrors();
  }
}
