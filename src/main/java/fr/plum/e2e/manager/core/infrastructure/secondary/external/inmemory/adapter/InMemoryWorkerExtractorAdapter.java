package fr.plum.e2e.manager.core.infrastructure.secondary.external.inmemory.adapter;

import fr.plum.e2e.manager.core.domain.model.dto.report.Report;
import fr.plum.e2e.manager.core.domain.model.exception.ArtifactReportException;
import fr.plum.e2e.manager.core.domain.port.WorkerExtractorPort;
import java.util.Collections;
import java.util.List;
import lombok.Setter;

@Setter
public class InMemoryWorkerExtractorAdapter implements WorkerExtractorPort {
  private List<Report> reports = Collections.emptyList();
  private boolean throwError = false;

  public void clear() {
    reports = Collections.emptyList();
    throwError = false;
  }

  @Override
  public List<Report> extractWorkerReportArtifacts(Object artefact) {
    if (throwError) {
      throw new ArtifactReportException();
    }
    return reports;
  }
}
