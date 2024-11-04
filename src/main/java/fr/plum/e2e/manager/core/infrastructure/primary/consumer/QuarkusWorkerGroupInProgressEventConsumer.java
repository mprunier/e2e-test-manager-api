package fr.plum.e2e.manager.core.infrastructure.primary.consumer;

import fr.plum.e2e.manager.core.application.SuiteFacade;
import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.WorkerType;
import fr.plum.e2e.manager.core.domain.model.event.WorkerGroupInProgressEvent;
import fr.plum.e2e.manager.core.domain.model.query.CommonQuery;
import fr.plum.e2e.manager.core.domain.model.query.SearchSuiteConfigurationQuery;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteWithWorkerView;
import fr.plum.e2e.manager.core.infrastructure.primary.rest.dto.response.WorkerUnitResponse;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.adapter.WebSocketNotifier;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerGroupIsInProgressNotificationEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.notification.dto.WorkerGroupTypeAllUpdatedEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class QuarkusWorkerGroupInProgressEventConsumer {

  private final SuiteFacade suiteFacade;
  private final WorkerFacade workerFacade;

  private final WebSocketNotifier webSocketNotifier;

  public void workerGroupInProgress(@ObservesAsync WorkerGroupInProgressEvent event) {

    var suiteWithWorker = getConfigurationSuiteWithWorkerView(event);

    var workerGroupIsInProgressNotificationEvent =
        WorkerGroupIsInProgressNotificationEvent.builder()
            .environmentId(event.environmentId())
            .workerType(event.worker().getType())
            .suiteWithWorker(suiteWithWorker)
            .build();
    webSocketNotifier.notifySubscribers(workerGroupIsInProgressNotificationEvent);

    buildAndSendWorkerGroupTypeAllUpdatedEvent(event);
  }

  private void buildAndSendWorkerGroupTypeAllUpdatedEvent(WorkerGroupInProgressEvent event) {
    if (WorkerType.ALL.equals(event.worker().getType())) {
      var optionalWorkerGroup = workerFacade.get(new CommonQuery(event.environmentId()));
      if (optionalWorkerGroup.isPresent()) {
        var workerGroupTypeAllUpdatedEvent =
            WorkerGroupTypeAllUpdatedEvent.builder()
                .workers(WorkerUnitResponse.fromWorkers(optionalWorkerGroup.get().getWorkerUnits()))
                .build();
        webSocketNotifier.notifySubscribers(workerGroupTypeAllUpdatedEvent);
      }
    }
  }

  private ConfigurationSuiteWithWorkerView getConfigurationSuiteWithWorkerView(
      WorkerGroupInProgressEvent event) {
    ConfigurationSuiteWithWorkerView suiteWithWorker = null;
    if (WorkerType.SUITE.equals(event.worker().getType())
        || WorkerType.TEST.equals(event.worker().getType())) {
      var searchSuiteQuery =
          SearchSuiteConfigurationQuery.builder()
              .suiteConfigurationId(
                  event
                      .worker()
                      .getWorkerUnits()
                      .getFirst()
                      .getFilter()
                      .suiteConfiguration()
                      .getId())
              .testConfigurationId(
                  event
                      .worker()
                      .getWorkerUnits()
                      .getFirst()
                      .getFilter()
                      .testConfiguration()
                      .getId())
              .build();
      var suitesPaginated = suiteFacade.searchSuites(searchSuiteQuery);
      if (suitesPaginated != null && !suitesPaginated.getContent().isEmpty()) {
        suiteWithWorker = suitesPaginated.getContent().getFirst();
      }
    }
    return suiteWithWorker;
  }
}
