package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerId;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import lombok.Builder;

@Builder
public record CancelWorkerCommand(ActionUsername actionUsername, WorkerId workerId) {}
