package fr.plum.e2e.manager.core.infrastructure.secondary.messaging.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.event.DomainEvent;
import fr.plum.e2e.manager.core.domain.port.EventPublisherPort;
import java.util.ArrayList;
import java.util.List;

public class InMemoryEventPublisherAdapter implements EventPublisherPort {
  private final List<DomainEvent> events = new ArrayList<>();

  @Override
  public void publishAsync(DomainEvent event) {
    events.add(event);
  }

  @Override
  public void publish(DomainEvent event) {
    events.add(event);
  }

  public List<DomainEvent> getPublishedEvents() {
    return events;
  }
}
