package fr.plum.e2e.manager.core.infrastructure.primary.scheduler;

public class RunWorkerScheduler {}

// @Slf4j
// @ApplicationScoped
// @RequiredArgsConstructor
// public class RunAllTestsScheduler {
//
//  private final RunAllTestsUseCase runAllTestsUseCase;
//  private final ConfigurationSchedulerRepository configurationSchedulerRepository;
//
//  private final AtomicBoolean inProgress = new AtomicBoolean(false);
//
//  @Scheduled(cron = "30 * * * * ?")
//  @ActivateRequestContext
//  public void execute() {
//    if (inProgress.compareAndSet(false, true)) {
//      try {
//        var configurationSchedulers = configurationSchedulerRepository.findAllEnabled();
//        var zoneId = ZoneId.systemDefault();
//        var now = ZonedDateTime.now(zoneId);
//
//        for (var configurationScheduler : configurationSchedulers) {
//          var scheduledTime =
// configurationScheduler.getScheduledTime().withZoneSameInstant(zoneId);
//          if (configurationScheduler.getDaysOfWeek().contains(now.getDayOfWeek())
//              && scheduledTime.getHour() == now.getHour()
//              && scheduledTime.getMinute() == now.getMinute()) {
//            runAllTestsUseCase.execute(configurationScheduler.getEnvironment().getId(), "System");
//          }
//        }
//      } finally {
//        inProgress.set(false);
//      }
//    }
//  }
// }
