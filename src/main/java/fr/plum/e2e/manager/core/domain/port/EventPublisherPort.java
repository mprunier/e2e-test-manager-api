package fr.plum.e2e.manager.core.domain.port;

import fr.plum.e2e.manager.core.domain.model.event.DomainEvent;

public interface EventPublisherPort {
  void publishAsync(DomainEvent event);

  void publish(DomainEvent event);
}
