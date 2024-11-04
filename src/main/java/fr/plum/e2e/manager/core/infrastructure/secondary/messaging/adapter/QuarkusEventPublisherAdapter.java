package fr.plum.e2e.manager.core.infrastructure.secondary.messaging.adapter;

import fr.plum.e2e.manager.core.domain.model.event.DomainEvent;
import fr.plum.e2e.manager.core.domain.port.out.EventPublisherPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

@ApplicationScoped
public class QuarkusEventPublisherAdapter implements EventPublisherPort {

  @Inject Event<DomainEvent> domainEventPublisher;

  @Override
  public void publishAsync(DomainEvent event) {
    domainEventPublisher.fireAsync(event);
  }

  @Override
  public void publish(DomainEvent event) {
    domainEventPublisher.fire(event);
  }
}
