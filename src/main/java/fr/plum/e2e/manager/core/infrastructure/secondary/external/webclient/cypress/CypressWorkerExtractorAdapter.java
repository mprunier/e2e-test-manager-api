package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.extractor.CypressArtifactsExtractor.extractArtifact;

import fr.plum.e2e.manager.core.domain.model.dto.report.Report;
import fr.plum.e2e.manager.core.domain.model.exception.ArtifactReportException;
import fr.plum.e2e.manager.core.domain.port.WorkerExtractorPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.cypress.mapper.WorkerReportMapper;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CypressWorkerExtractorAdapter implements WorkerExtractorPort {

  @Override
  public List<Report> extractWorkerReportArtifacts(Object artifacts) {
    var artifactData = extractArtifact(artifacts);

    if (artifactData.getReport() == null
        || artifactData.getReport().getResults() == null
        || artifactData.getReport().getResults().isEmpty()) {
      throw new ArtifactReportException();
    }

    return artifactData.getReport().getResults().stream()
        .map(
            mochaReportResultInternal ->
                WorkerReportMapper.convertToWorkerReportResult(
                    mochaReportResultInternal,
                    artifactData.getScreenshots(),
                    artifactData.getVideos()))
        .toList();
  }
}
