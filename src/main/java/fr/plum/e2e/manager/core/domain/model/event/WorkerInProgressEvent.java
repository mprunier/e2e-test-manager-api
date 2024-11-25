package fr.plum.e2e.manager.core.domain.model.event;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.worker.Worker;
import lombok.Builder;

@Builder
public record WorkerInProgressEvent(
    EnvironmentId environmentId, ActionUsername username, Worker worker) implements DomainEvent {}
