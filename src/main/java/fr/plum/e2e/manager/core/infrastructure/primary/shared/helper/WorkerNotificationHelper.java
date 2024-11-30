package fr.plum.e2e.manager.core.infrastructure.primary.shared.helper;

import fr.plum.e2e.manager.core.application.SuiteFacade;
import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.adapter.WebSocketNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerNotificationStatus;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerUnitUpdatedNotificationEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerUpdatedNotificationEvent;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class WorkerNotificationHelper {

  private final WorkerFacade workerFacade;
  private final SuiteFacade suiteFacade;

  private final WebSocketNotifier webSocketNotifier;

  public void sendWorkerUnitUpdatedNotification(EnvironmentId environmentId, Worker worker) {
    if (WorkerType.ALL.equals(worker.getType())) {
      var optionalWorker = workerFacade.get(new CommonQuery(environmentId));
      if (optionalWorker.isPresent()) {
        var workerUnitUpdatedNotificationEvent =
            WorkerUnitUpdatedNotificationEvent.builder()
                .environmentId(environmentId)
                .workers(WorkerUnitResponse.fromWorkers(optionalWorker.get().getWorkerUnits()))
                .build();
        webSocketNotifier.notifySubscribers(workerUnitUpdatedNotificationEvent);
      }
    }
  }

  public void sendWorkerUpdatedNotification(
      EnvironmentId environmentId, Worker worker, WorkerNotificationStatus status) {
    var configurationSuiteWithWorkerView = getConfigurationSuiteWithWorkerView(worker);

    var workerUpdatedNotificationEvent =
        WorkerUpdatedNotificationEvent.builder()
            .environmentId(environmentId)
            .workerType(worker.getType())
            .status(status)
            .configurationSuiteWithWorkerView(configurationSuiteWithWorkerView)
            .build();
    webSocketNotifier.notifySubscribers(workerUpdatedNotificationEvent);
  }

  private ConfigurationSuiteWithWorkerView getConfigurationSuiteWithWorkerView(Worker worker) {
    ConfigurationSuiteWithWorkerView suiteWithWorker = null;
    if (WorkerType.SUITE.equals(worker.getType()) || WorkerType.TEST.equals(worker.getType())) {
      var searchSuiteQuery =
          SearchSuiteConfigurationQuery.builder()
              .suiteConfigurationId(
                  worker
                      .getWorkerUnits()
                      .getFirst()
                      .getFilter()
                      .suiteFilter()
                      .suiteConfigurationId())
              .testConfigurationId(
                  worker.getWorkerUnits().getFirst().getFilter().testFilter().testConfigurationId())
              .build();
      var suitesPaginated = suiteFacade.searchSuites(searchSuiteQuery);
      if (suitesPaginated != null && !suitesPaginated.getContent().isEmpty()) {
        suiteWithWorker = suitesPaginated.getContent().getFirst();
      }
    }
    return suiteWithWorker;
  }
}
