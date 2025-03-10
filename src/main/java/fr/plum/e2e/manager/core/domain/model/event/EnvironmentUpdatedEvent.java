package fr.plum.e2e.manager.core.domain.model.event;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;

public record EnvironmentUpdatedEvent(EnvironmentId environmentId, ActionUsername username)
    implements DomainEvent {}
