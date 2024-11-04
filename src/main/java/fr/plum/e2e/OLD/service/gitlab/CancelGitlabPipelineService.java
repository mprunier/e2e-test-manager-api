package fr.plum.e2e.OLD.service.gitlab;

import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CancelGitlabPipelineService {

  @RestClient private GitlabClient gitlabClient;

  public void cancelPipeline(String token, String projectId, String pipelineId) {
    gitlabClient.cancelPipeline(token, projectId, pipelineId);
  }
}
