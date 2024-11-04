package fr.plum.e2e.manager.sharedkernel.domain.port.out;

import java.time.ZonedDateTime;

public interface ClockPort {
  ZonedDateTime now();
}
