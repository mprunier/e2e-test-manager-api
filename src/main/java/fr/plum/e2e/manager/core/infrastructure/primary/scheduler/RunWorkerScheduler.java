package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

import fr.plum.e2e.manager.core.application.WorkerFacade;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.command.RunWorkerCommand;
import fr.plum.e2e.manager.core.domain.port.out.repository.SchedulerConfigurationRepositoryPort;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Slf4j
@Startup
@ApplicationScoped
public class RunWorkerScheduler {

  @ConfigProperty(name = "scheduler.enabled", defaultValue = "true")
  boolean enabled;

  private final WorkerFacade workerFacade;
  private final SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort;
  private final Map<EnvironmentId, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
  private final ScheduledExecutorService schedulerExecutor = Executors.newScheduledThreadPool(1);

  public RunWorkerScheduler(
      WorkerFacade workerFacade,
      SchedulerConfigurationRepositoryPort schedulerConfigurationRepositoryPort) {
    this.workerFacade = workerFacade;
    this.schedulerConfigurationRepositoryPort = schedulerConfigurationRepositoryPort;
  }

  @PostConstruct
  public void init() {
    if (!enabled) {
      log.info("RunWorkerScheduler is disabled by configuration.");
      return;
    }
    log.info("Initializing RunWorkerScheduler...");
    updateSchedule();
  }

  public void updateSchedule() {
    if (!enabled) {
      log.debug("Scheduler is disabled, ignoring update request.");
      return;
    }

    log.info("Starting schedule update...");
    var configurations = schedulerConfigurationRepositoryPort.findAll();

    scheduledTasks.forEach((id, future) -> future.cancel(false));
    scheduledTasks.clear();

    configurations.forEach(
        scheduler -> {
          if (!scheduler.getIsEnabled().value()) {
            log.info("Scheduler for environment [{}] is disabled.", scheduler.getId().value());
            return;
          }
          scheduleNextExecution(scheduler);
        });

    logScheduledTasks();
  }

  private void scheduleNextExecution(SchedulerConfiguration scheduler) {
    if (!scheduler.getIsEnabled().value()) {
      return;
    }

    var scheduledTime = LocalTime.of(scheduler.getHour().value(), scheduler.getMinute().value(), 0);
    LocalDateTime nextRun = findNextExecutionTime(LocalDateTime.now(), scheduledTime, scheduler);

    if (nextRun != null) {
      long initialDelay = Duration.between(LocalDateTime.now(), nextRun).toMillis();
      ScheduledFuture<?> task =
          schedulerExecutor.schedule(
              () -> {
                try {
                  if (scheduler.getIsEnabled().value()) {
                    executeTask(scheduler);
                    scheduleNextExecution(scheduler);
                    logScheduledTasks();
                  }
                } catch (Exception e) {
                  log.error(
                      "Error executing task for environment id [{}].",
                      scheduler.getId().value(),
                      e);
                  scheduleNextExecution(scheduler);
                  logScheduledTasks();
                }
              },
              initialDelay,
              TimeUnit.MILLISECONDS);

      scheduledTasks.put(scheduler.getId(), task);
    }
  }

  private void logScheduledTasks() {
    if (scheduledTasks.isEmpty()) {
      log.info("No active scheduled tasks.");
      return;
    }

    log.info("Current scheduled tasks:");
    scheduledTasks.forEach(
        (envId, future) -> {
          long delayInMillis = future.getDelay(TimeUnit.MILLISECONDS);
          LocalDateTime scheduledTime = LocalDateTime.now().plus(Duration.ofMillis(delayInMillis));
          log.info("Environment [{}] scheduled for: {}.", envId.value(), scheduledTime);
        });
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

  private void executeTask(SchedulerConfiguration scheduler) {
    log.debug("Executing worker for environment id [{}]...", scheduler.getId().value());
    var command =
        RunWorkerCommand.builder()
            .environmentId(scheduler.getId())
            .username(new ActionUsername("Scheduler"))
            .build();

    workerFacade.run(command);
  }

  @PreDestroy
  public void shutdown() {
    if (!enabled) {
      return;
    }

    log.info("Shutting down RunWorkerScheduler...");
    scheduledTasks.values().forEach(future -> future.cancel(false));
    schedulerExecutor.shutdown();

    try {
      if (!schedulerExecutor.awaitTermination(1, TimeUnit.MINUTES)) {
        log.warn("Some tasks did not terminate within 1 minute, forcing shutdown.");
        schedulerExecutor.shutdownNow();
      }
    } catch (InterruptedException e) {
      log.warn("Shutdown interrupted, forcing immediate shutdown.");
      Thread.currentThread().interrupt();
      schedulerExecutor.shutdownNow();
    }
  }
}
