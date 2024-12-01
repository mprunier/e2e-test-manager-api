package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.DayOfWeek;
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
  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final Map<EnvironmentId, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService schedulerExecutor = Executors.newScheduledThreadPool(1);

  @PostConstruct
  void init() {
    updateSchedule();
  }

  public void updateSchedule() {
    var existingTasks = new HashMap<>(scheduledTasks);
    scheduledTasks.clear();

    var schedulers = schedulerConfigurationRepositoryPort.findAll();
    schedulers.forEach(
        scheduler -> {
          if (!scheduler.getIsEnabled().value()) {
            log.debug(
                "Scheduler for environment id [{}] is disabled, skipping...",
                scheduler.getId().value());
            ScheduledFuture<?> existingTask = existingTasks.get(scheduler.getId());
            if (existingTask != null) {
              existingTask.cancel(false);
              log.debug(
                  "Cancelled existing task for disabled scheduler id [{}]",
                  scheduler.getId().value());
            }
            return;
          }

          log.debug("Configuring scheduler for environment id [{}]", scheduler.getId().value());
          ScheduledFuture<?> existingTask = existingTasks.get(scheduler.getId());

          boolean isTaskActiveOrRunning =
              existingTask != null && !existingTask.isCancelled() && !existingTask.isDone();
          if (isTaskActiveOrRunning) {
            log.debug(
                "Keeping existing active task for environment id [{}]", scheduler.getId().value());
            scheduledTasks.put(scheduler.getId(), existingTask);
          } else {
            log.debug("Scheduling new task for environment id [{}]", scheduler.getId().value());
            scheduleNextExecution(scheduler);
          }
        });

    existingTasks.forEach(
        (id, task) -> {
          if (!scheduledTasks.containsKey(id)) {
            task.cancel(false);
            log.debug("Cancelled outdated task for environment id [{}]", id.value());
          }
        });
  }

  private void scheduleNextExecution(SchedulerConfiguration scheduler) {
    if (!scheduler.getIsEnabled().value()) {
      log.debug(
          "Scheduler id [{}] is disabled, skipping execution scheduling",
          scheduler.getId().value());
      return;
    }

    var scheduledTime = LocalTime.of(scheduler.getHour().value(), scheduler.getMinute().value(), 0);
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextRun = findNextExecutionTime(now, scheduledTime, scheduler);

    if (nextRun != null) {
      long initialDelay = Duration.between(now, nextRun).toMillis();
      log.debug(
          "Next execution for environment id [{}] scheduled at: {}",
          scheduler.getId().value(),
          nextRun);

      ScheduledFuture<?> task =
          schedulerExecutor.schedule(
              () -> {
                if (scheduler.getIsEnabled().value()) {
                  executeTask(scheduler);
                  scheduleNextExecution(scheduler);
                } else {
                  log.info(
                      "Skipping execution for disabled scheduler id [{}]",
                      scheduler.getId().value());
                }
              },
              initialDelay,
              TimeUnit.MILLISECONDS);

      scheduledTasks.put(scheduler.getId(), task);
    }
  }

  private LocalDateTime findNextExecutionTime(
      LocalDateTime now, LocalTime scheduledTime, SchedulerConfiguration scheduler) {
    LocalDateTime candidate = LocalDateTime.of(now.toLocalDate(), scheduledTime);

    if (now.isAfter(candidate)) {
      candidate = candidate.plusDays(1);
    }

    for (int i = 0; i < 7; i++) {
      DayOfWeek candidateDay = candidate.getDayOfWeek();
      if (scheduler.getDaysOfWeek().list().contains(candidateDay)) {
        return candidate;
      }
      candidate = candidate.plusDays(1);
    }

    return null;
  }

  private void executeTask(SchedulerConfiguration schedulerConfiguration) {
    try {
      log.debug("Executing worker for environment id [{}]", schedulerConfiguration.getId().value());

      var command =
          RunWorkerCommand.builder()
              .environmentId(schedulerConfiguration.getId())
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
