package fr.plum.e2e.manager.core.infrastructure.secondary.clock.system.adapter;

import fr.plum.e2e.manager.sharedkernel.domain.port.ClockPort;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.ZonedDateTime;

@ApplicationScoped
public class SystemClockAdapter implements ClockPort {

  @Override
  public ZonedDateTime now() {
    return ZonedDateTime.now();
  }
}
