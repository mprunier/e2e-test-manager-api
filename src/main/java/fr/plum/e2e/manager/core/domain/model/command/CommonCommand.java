package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import lombok.Builder;

@Builder
public record CommonCommand(EnvironmentId environmentId, ActionUsername username) {}
