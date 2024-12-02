package fr.plum.e2e.manager.core.domain.model.event;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import java.util.List;

public record EnvironmentSynchronizedEvent(
    EnvironmentId environmentId,
    ActionUsername username,
    List<SynchronizationError> synchronizationErrors)
    implements DomainEvent {}
