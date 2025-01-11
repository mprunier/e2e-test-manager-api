package fr.plum.e2e.manager.core.application.command.schedulerconfiguration;

import static org.junit.jupiter.api.Assertions.*;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.SchedulerConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerDaysOfWeek;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerHour;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.schedulerconfiguration.vo.SchedulerMinute;
import fr.plum.e2e.manager.core.domain.model.command.UpdateSchedulerCommand;
import fr.plum.e2e.manager.core.domain.model.event.SchedulerUpdatedEvent;
import fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter.InMemoryClockAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter.InMemoryEventPublisherAdapter;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.inmemory.adapter.repository.InMemorySchedulerConfigurationRepositoryAdapter;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.DayOfWeek;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UpdateSchedulerConfigurationCommandHandlerTest {

  private static final EnvironmentId ENVIRONMENT_ID =
      new EnvironmentId(java.util.UUID.randomUUID());
  private static final ActionUsername ACTION_USERNAME = new ActionUsername("Test User");
  private static final SchedulerIsEnabled ENABLED = new SchedulerIsEnabled(true);
  private static final SchedulerDaysOfWeek DAYS_OF_WEEK =
      new SchedulerDaysOfWeek(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
  private static final SchedulerHour HOUR = new SchedulerHour(10);
  private static final SchedulerMinute MINUTE = new SchedulerMinute(30);

  private UpdateSchedulerConfigurationCommandHandler handler;
  private InMemorySchedulerConfigurationRepositoryAdapter schedulerRepository;
  private InMemoryEventPublisherAdapter eventPublisher;
  private InMemoryClockAdapter clock;

  @BeforeEach
  void setUp() {
    schedulerRepository = new InMemorySchedulerConfigurationRepositoryAdapter();
    eventPublisher = new InMemoryEventPublisherAdapter();
    clock = new InMemoryClockAdapter();

    handler =
        new UpdateSchedulerConfigurationCommandHandler(schedulerRepository, eventPublisher, clock);

    var initialConfig = SchedulerConfiguration.create(ENVIRONMENT_ID, createAuditInfo());
    schedulerRepository.save(initialConfig);
  }

  @Test
  void should_update_scheduler_with_valid_command() {
    // Given
    var command = createValidCommand();
    var expectedDateTime = clock.now();

    // When
    handler.execute(command);

    // Then
    var updatedScheduler = schedulerRepository.find(ENVIRONMENT_ID);
    assertTrue(updatedScheduler.isPresent());

    var scheduler = updatedScheduler.get();
    assertEquals(command.isEnabled().value(), scheduler.getIsEnabled().value());
    assertEquals(command.daysOfWeek().list(), scheduler.getDaysOfWeek().list());
    assertEquals(command.schedulerHour().value(), scheduler.getHour().value());
    assertEquals(command.schedulerMinute().value(), scheduler.getMinute().value());
    assertEquals(ACTION_USERNAME.value(), scheduler.getAuditInfo().getUpdatedBy().value());
    assertEquals(expectedDateTime, scheduler.getAuditInfo().getUpdatedAt());

    // Verify event was published
    assertEquals(1, eventPublisher.getPublishedEvents().size());
    var event = (SchedulerUpdatedEvent) eventPublisher.getPublishedEvents().getFirst();
    assertEquals(ENVIRONMENT_ID, event.environmentId());
    assertEquals(ACTION_USERNAME, event.username());
  }

  private UpdateSchedulerCommand createValidCommand() {
    return new UpdateSchedulerCommand(
        ENVIRONMENT_ID, ENABLED, DAYS_OF_WEEK, HOUR, MINUTE, ACTION_USERNAME);
  }

  private AuditInfo createAuditInfo() {
    return AuditInfo.create(ACTION_USERNAME, clock.now());
  }
}
