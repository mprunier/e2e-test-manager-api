package fr.njj.galaxion.endtoendtesting.service.gitlab;

import fr.njj.galaxion.endtoendtesting.client.gitlab.GitlabClient;
import fr.njj.galaxion.endtoendtesting.client.gitlab.response.GitlabResponse;
import fr.njj.galaxion.endtoendtesting.domain.exception.MoreOneJobException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RetrieveGitlabJobService {

    @RestClient
    private GitlabClient gitlabClient;

    public GitlabResponse getJob(String token, String projectId, String pipelineId) {
        var jobs = gitlabClient.getJobs(token, projectId, pipelineId);
        if (jobs.size() > 1) {
            throw new MoreOneJobException(pipelineId);
        }
        return jobs.getFirst();
    }
}

