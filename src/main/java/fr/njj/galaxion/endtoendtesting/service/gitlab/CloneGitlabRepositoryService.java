package fr.njj.galaxion.endtoendtesting.service.gitlab;

import fr.njj.galaxion.endtoendtesting.client.gitlab.GitlabClient;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class CloneGitlabRepositoryService {

    @RestClient
    private GitlabClient gitlabClient;

    public File cloneRepo(String projectId, String uniqueId, String gitlabToken, String branch) {
        var repoUrl = getRepoUrl(gitlabToken, projectId);
        var credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", gitlabToken);
        var tempDirectory = new File("./tmp/config/sync/" + uniqueId);
        try (Git ignored = Git
                .cloneRepository()
                .setURI(repoUrl)
                .setDirectory(tempDirectory)
                .setBranch(branch)
                .setCredentialsProvider(credentialsProvider)
                .call()) {
            return tempDirectory;
        } catch (GitAPIException exception) {
            throw new ConfigurationSynchronizationException("Clone Repo Error : " + exception.getMessage());
        }
    }

    private String getRepoUrl(String token, String projectId) {
        return gitlabClient.getProjectDetail(token, projectId).getRepoUrl();
    }
}

