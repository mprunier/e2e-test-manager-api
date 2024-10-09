package fr.njj.galaxion.endtoendtesting.service.gitlab;

import static fr.njj.galaxion.endtoendtesting.helper.GitHelper.extractArtifact;

import fr.njj.galaxion.endtoendtesting.client.gitlab.GitlabClient;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
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
    var artifactDataInternal = new ArtifactDataInternal();
    try {
      var zipArtifacts = gitlabClient.getJobArtifacts(token, projectId, jobId);
      extractArtifact(artifactDataInternal, zipArtifacts);
    } catch (Exception e) {
      log.error("Error during retrieve artifacts in job id [{}]", jobId, e);
    }
    return artifactDataInternal;
  }
}
