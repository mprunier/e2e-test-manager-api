package fr.plum.e2e.manager.sharedkernel.domain.port;

import java.time.ZonedDateTime;

public interface ClockPort {
  ZonedDateTime now();
}
