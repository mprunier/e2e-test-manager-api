package fr.plum.e2e.manager.core.domain.model.event;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;

public record WorkerCanceledEvent(
    EnvironmentId environmentId, ActionUsername username, Worker worker) implements DomainEvent {}
