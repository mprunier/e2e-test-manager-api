package fr.plum.e2e.OLD.service.gitlab;

import static fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.CypressArtifactsExtractor.extractArtifact;

import fr.plum.e2e.manager.core.infrastructure.secondary.external.cypress.extractor.dto.ArtifactDataInternal;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveGitlabJobArtifactsService {

  @RestClient private GitlabClient gitlabClient;

  public ArtifactDataInternal getArtifactData(String token, String projectId, String jobId) {
    try {
      var zipArtifacts = gitlabClient.getJobArtifacts(token, projectId, jobId);
      return extractArtifact(zipArtifacts);
    } catch (Exception e) {
      log.error("Error during retrieve artifacts in job id [{}]", jobId, e);
      return new ArtifactDataInternal();
    }
  }
}
