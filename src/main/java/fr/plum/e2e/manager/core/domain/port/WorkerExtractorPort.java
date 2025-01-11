package fr.plum.e2e.manager.core.domain.port;

import fr.plum.e2e.manager.core.domain.model.dto.report.Report;
import java.util.List;

public interface WorkerExtractorPort {

  List<Report> extractWorkerReportArtifacts(Object artefact);
}
