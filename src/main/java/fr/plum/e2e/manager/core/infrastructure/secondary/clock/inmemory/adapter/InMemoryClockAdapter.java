package fr.plum.e2e.manager.core.infrastructure.secondary.clock.inmemory.adapter;

import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import java.time.ZonedDateTime;

public class InMemoryClockAdapter implements ClockPort {

  public static final int YEAR = 1992;
  public static final int MONTH = 5;
  public static final int DAY = 23;
  public static final int HOUR = 15;
  public static final int MINUTE = 30;
  public static final int SECOND = 0;
  public static final int NANO = 0;

  @Override
  public ZonedDateTime now() {
    return ZonedDateTime.of(
        YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, NANO, ZonedDateTime.now().getZone());
  }
}
