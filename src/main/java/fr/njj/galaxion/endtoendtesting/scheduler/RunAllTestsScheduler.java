package fr.njj.galaxion.endtoendtesting.scheduler;

import fr.njj.galaxion.endtoendtesting.model.repository.ConfigurationSchedulerRepository;
import fr.njj.galaxion.endtoendtesting.usecases.run.RunAllTestsUseCase;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunAllTestsScheduler {

  private final RunAllTestsUseCase runAllTestsUseCase;
  private final ConfigurationSchedulerRepository configurationSchedulerRepository;

  private final AtomicBoolean inProgress = new AtomicBoolean(false);

  @Scheduled(cron = "30 * * * * ?")
  @ActivateRequestContext
  public void execute() {
    if (inProgress.compareAndSet(false, true)) {
      try {
        var configurationSchedulers = configurationSchedulerRepository.findAllEnabled();
        var zoneId = ZoneId.systemDefault();
        var now = ZonedDateTime.now(zoneId);

        for (var configurationScheduler : configurationSchedulers) {
          var scheduledTime = configurationScheduler.getScheduledTime().withZoneSameInstant(zoneId);
          if (configurationScheduler.getDaysOfWeek().contains(now.getDayOfWeek())
              && scheduledTime.getHour() == now.getHour()
              && scheduledTime.getMinute() == now.getMinute()) {
            runAllTestsUseCase.execute(configurationScheduler.getEnvironment().getId(), "System");
          }
        }
      } finally {
        inProgress.set(false);
      }
    }
  }
}
