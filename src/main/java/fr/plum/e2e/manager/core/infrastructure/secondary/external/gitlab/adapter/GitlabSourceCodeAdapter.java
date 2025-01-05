package fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.adapter;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.synchronization.vo.SourceCodeProject;
import fr.plum.e2e.manager.core.domain.model.exception.SourceCodeCloneException;
import fr.plum.e2e.manager.core.domain.port.SourceCodePort;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.gitlab.client.GitlabClient;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.File;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GitlabSourceCodeAdapter implements SourceCodePort {

  @RestClient private GitlabClient gitlabClient;

  @Override
  public SourceCodeProject cloneRepository(SourceCodeInformation sourceCodeInformation) {
    String repoUrl;
    try {
      repoUrl =
          gitlabClient
              .getProjectDetail(sourceCodeInformation.token(), sourceCodeInformation.projectId())
              .getRepoUrl();
    } catch (CustomException e) {
      log.error("Error during fetching project detail from Gitlab.");
      throw e;
    }
    var credentialsProvider =
        new UsernamePasswordCredentialsProvider("oauth2", sourceCodeInformation.token());
    var tempDirectory =
        new File(
            "./tmp/config/sync/" + sourceCodeInformation.projectId() + "/" + UUID.randomUUID());
    try (Git ignored =
        Git.cloneRepository()
            .setURI(repoUrl)
            .setDirectory(tempDirectory)
            .setBranch(sourceCodeInformation.branch())
            .setCredentialsProvider(credentialsProvider)
            .call()) {
      return new SourceCodeProject(tempDirectory);
    } catch (GitAPIException exception) {
      throw new SourceCodeCloneException(exception.getMessage());
    }
  }
}
