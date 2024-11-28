package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.scheduler.Scheduler;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerRepositoryPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@AllArgsConstructor
public class RunWorkerScheduler {

  private final WorkerFacade workerFacade;
  private final SchedulerRepositoryPort schedulerRepositoryPort;
  private final Map<EnvironmentId, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService schedulerExecutor = Executors.newScheduledThreadPool(1);

  @PostConstruct
  void init() {
    updateSchedule();
  }

  public void updateSchedule() {
    var existingTasks = new HashMap<>(scheduledTasks);
    scheduledTasks.clear();

    var schedulers = schedulerRepositoryPort.findAll();
    schedulers.forEach(
        scheduler -> {
          ScheduledFuture<?> existingTask = existingTasks.get(scheduler.getId());

          boolean isTaskActiveOrRunning = !existingTask.isCancelled() && !existingTask.isDone();
          if (isTaskActiveOrRunning) {
            scheduledTasks.put(scheduler.getId(), existingTask);
          } else {
            var scheduledTime =
                LocalTime.of(scheduler.getHour().value(), scheduler.getMinute().value(), 0);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = LocalDateTime.of(now.toLocalDate(), scheduledTime);
            if (now.isAfter(nextRun)) {
              nextRun = nextRun.plusDays(1);
            }

            long initialDelay = Duration.between(now, nextRun).toMillis();
            ScheduledFuture<?> task =
                schedulerExecutor.scheduleAtFixedRate(
                    () -> executeTask(scheduler),
                    initialDelay,
                    TimeUnit.DAYS.toMillis(1),
                    TimeUnit.MILLISECONDS);

            scheduledTasks.put(scheduler.getId(), task);
          }
        });

    existingTasks.forEach(
        (id, task) -> {
          if (!scheduledTasks.containsKey(id)) {
            task.cancel(false);
          }
        });
  }

  private void executeTask(Scheduler scheduler) {
    try {
      log.info("Executing worker for environment id [{}]", scheduler.getId().value());

      var command =
          RunWorkerCommand.builder()
              .environmentId(scheduler.getId())
              .username(new ActionUsername("Scheduler"))
              .build();
      workerFacade.run(command);

    } catch (Exception e) {
      log.error("Error executing task", e);
    }
  }

  @PreDestroy
  void shutdown() {
    schedulerExecutor.shutdown();
  }
}
