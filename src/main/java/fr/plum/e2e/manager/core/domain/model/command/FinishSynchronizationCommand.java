package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.ActionUsername;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SynchronizationError;
import java.util.List;
import lombok.Builder;

@Builder
public record FinishSynchronizationCommand(
    EnvironmentId environmentId,
    ActionUsername username,
    List<SynchronizationError> synchronizationErrors) {}
