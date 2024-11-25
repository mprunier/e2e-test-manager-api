package fr.plum.e2e.manager.core.domain.port.out;

import fr.plum.e2e.manager.core.domain.model.aggregate.report.Report;
import java.util.List;

public interface WorkerExtractorPort {

  List<Report> extractWorkerReportArtifacts(Object artefact);
}
