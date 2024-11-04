package fr.plum.e2e.manager.core.domain.model.command;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.WorkerUnitId;
import lombok.Builder;

@Builder
public record ReportWorkerCommand(WorkerUnitId workerUnitId) {}
