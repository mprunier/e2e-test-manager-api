package fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.CypressArtifactsExtractor.extractArtifact;

import fr.plum.e2e.manager.core.domain.model.aggregate.worker.vo.report.Report;
import fr.plum.e2e.manager.core.domain.port.out.WorkerExtractorPort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.mapper.WorkerReportMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CypressWorkerExtractorAdapter implements WorkerExtractorPort {

  @Override
  public Report extractWorkerReportArtifacts(Object artifacts) {
    try {
      var artifactData = extractArtifact(artifacts);

      var results =
          artifactData.getReport().getResults().stream()
              .map(WorkerReportMapper::convertToWorkerReportResult)
              .toList();
      return Report.builder()
          .results(results)
          .videos(artifactData.getVideos())
          .screenshots(artifactData.getScreenshots())
          .build();
    } catch (Exception e) {
      log.error("Error during extract artifacts", e);
      return Report.builder().build();
    }
  }
}
