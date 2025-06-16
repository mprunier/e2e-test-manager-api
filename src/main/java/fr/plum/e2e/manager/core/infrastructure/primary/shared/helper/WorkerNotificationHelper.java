package fr.plum.e2e.manager.core.infrastructure.primary.shared.helper;

import fr.plum.e2e.manager.core.application.query.suite.SearchSuiteQueryHandler;
import fr.plum.e2e.manager.core.application.query.worker.GetTypeAllWorkerQueryHandler;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteWithWorkerProjection;
import fr.plum.e2e.manager.core.domain.model.projection.PaginatedProjection;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.ConfigurationSuiteWithWorkerResponse;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.adapter.EnvironmentNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.TypeAllWorkerUnitsUpdatedNotificationEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.WorkerNotificationStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.websocket.dto.WorkerUpdatedNotificationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class WorkerNotificationHelper {

  private final GetTypeAllWorkerQueryHandler getTypeAllWorkerQueryHandler;
  private final SearchSuiteQueryHandler searchSuiteQueryHandler;

  private final EnvironmentNotifier environmentNotifier;

  public void sendWorkerUnitUpdatedNotification(EnvironmentId environmentId, Worker worker) {
    if (WorkerType.ALL.equals(worker.getType())) {
      var optionalWorker = getTypeAllWorkerQueryHandler.execute(new CommonQuery(environmentId));
      if (optionalWorker.isPresent()) {
        var workerUnitUpdatedNotificationEvent =
            TypeAllWorkerUnitsUpdatedNotificationEvent.builder()
                .environmentId(environmentId.value())
                .workerUnits(WorkerUnitResponse.fromWorkers(optionalWorker.get().getWorkerUnits()))
                .build();
        environmentNotifier.notifySubscribers(workerUnitUpdatedNotificationEvent);
      }
    }
  }

  public void sendWorkerUpdatedNotification(
      EnvironmentId environmentId, Worker worker, WorkerNotificationStatus status) {
    var configurationSuiteWithWorkerProjection = getConfigurationSuiteWithWorkerView(worker);

    var workerUpdatedNotificationEvent =
        WorkerUpdatedNotificationEvent.builder()
            .environmentId(environmentId)
            .workerType(worker.getType())
            .status(status)
            .configurationSuiteWithWorker(
                configurationSuiteWithWorkerProjection != null
                    ? ConfigurationSuiteWithWorkerResponse.fromDomain(
                        configurationSuiteWithWorkerProjection)
                    : null)
            .build();
    environmentNotifier.notifySubscribers(workerUpdatedNotificationEvent);
  }

  private ConfigurationSuiteWithWorkerProjection getConfigurationSuiteWithWorkerView(
      Worker worker) {
    if (!isValidWorkerType(worker.getType())) {
      return null;
    }

    var firstWorkerUnit = worker.getWorkerUnits().getFirst();
    var filter = firstWorkerUnit.getFilter();

    var searchQuery =
        SearchSuiteConfigurationQuery.builder()
            .environmentId(worker.getEnvironmentId())
            .sortField("file")
            .sortOrder("asc")
            .page(0)
            .size(1)
            .suiteConfigurationId(
                WorkerType.SUITE.equals(worker.getType())
                    ? filter.suiteFilter().suiteConfigurationId()
                    : null)
            .testConfigurationId(
                WorkerType.TEST.equals(worker.getType())
                    ? filter.testFilter().testConfigurationId()
                    : null)
            .fileNames(WorkerType.GROUP.equals(worker.getType()) ? filter.fileNames() : null)
            .build();

    return Optional.ofNullable(searchSuiteQueryHandler.execute(searchQuery))
        .map(PaginatedProjection::getContent)
        .filter(content -> !content.isEmpty())
        .map(List::getFirst)
        .orElse(null);
  }

  private boolean isValidWorkerType(WorkerType type) {
    return WorkerType.SUITE.equals(type)
        || WorkerType.TEST.equals(type)
        || WorkerType.GROUP.equals(type);
  }
}
