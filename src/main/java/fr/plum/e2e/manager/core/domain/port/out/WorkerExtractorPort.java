package fr.plum.e2e.manager.core.domain.port.out;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report.Report;

public interface WorkerExtractorPort {

  Report extractWorkerReportArtifacts(Object artefact);
}
