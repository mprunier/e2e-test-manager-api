package fr.njj.galaxion.endtoendtesting.service.gitlab;

import fr.njj.galaxion.endtoendtesting.client.gitlab.GitlabClient;
import fr.njj.galaxion.endtoendtesting.client.gitlab.request.PipelineRequest;
import fr.njj.galaxion.endtoendtesting.client.gitlab.request.VariableRequest;
import fr.njj.galaxion.endtoendtesting.client.gitlab.response.GitlabResponse;
import fr.njj.galaxion.endtoendtesting.domain.exception.ConfigurationSynchronizationException;
import fr.njj.galaxion.endtoendtesting.domain.exception.MoreOneJobException;
import fr.njj.galaxion.endtoendtesting.domain.internal.ArtifactDataInternal;
import fr.njj.galaxion.endtoendtesting.model.entity.EnvironmentEntity;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.io.File;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;
import static fr.njj.galaxion.endtoendtesting.helper.GitlabHelper.extractArtifact;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class GitlabService {

    @RestClient
    private GitlabClient gitlabClient;

    public GitlabResponse runJob(String branch, String token, String projectId, String spec, String variables, String grep, boolean isVideo) {
        var pipelineRequestBuilder = PipelineRequest
                .builder()
                .variable(VariableRequest.builder().key("CYPRESS_TEST_ENABLED").value("true").build()) // Pour pas que le job tourne pour un commit lambda.
                .ref(branch);
        if (StringUtils.isNotBlank(spec)) {
            pipelineRequestBuilder.variable(VariableRequest.builder().key("CYPRESS_TEST_SPEC").value(START_PATH + spec).build());
        }
        if (StringUtils.isNotBlank(grep)) {
            pipelineRequestBuilder.variable(VariableRequest.builder().key("CYPRESS_TEST_GREP").value(grep).build());
        }
        if (StringUtils.isNotBlank(variables)) {
            pipelineRequestBuilder.variable(VariableRequest.builder().key("CYPRESS_VARIABLES").value(variables).build());
        }
        pipelineRequestBuilder.variable(VariableRequest.builder().key("CYPRESS_VIDEO").value(isVideo ? "true" : "false").build());
        return gitlabClient.runPipeline(token, projectId, pipelineRequestBuilder.build());
    }

    public GitlabResponse getJob(String token, String projectId, String pipelineId) {
        var jobs = gitlabClient.getJobs(token, projectId, pipelineId);
        if (jobs.size() > 1) {
            throw new MoreOneJobException(pipelineId);
        }
        return jobs.get(0);
    }

    public ArtifactDataInternal getArtifactData(String token, String projectId, String jobId) {
        var artifactDataInternal = new ArtifactDataInternal();
        try {
            var zipArtifacts = gitlabClient.getJobArtifacts(token, projectId, jobId);
            extractArtifact(artifactDataInternal, zipArtifacts);
        } catch (Exception e) {
            log.warn("Error during retrieve artifacts in job id [{}]", jobId);
        }
        return artifactDataInternal;
    }

    public void cancelPipeline(String token, String projectId, String pipelineId) {
        gitlabClient.cancelPipeline(token, projectId, pipelineId);
    }

    public String getRepoUrl(String token, String projectId) {
        return gitlabClient.getProjectDetail(token, projectId).getRepoUrl();
    }

    public File cloneRepo(EnvironmentEntity environment, String repoUrl) {
        try {
            var credentialsProvider = new UsernamePasswordCredentialsProvider("oauth2", environment.getToken());
            var tempDirectory = new File("./tmp/config/sync/" + environment.getId());
            Git.cloneRepository()
               .setURI(repoUrl)
               .setDirectory(tempDirectory)
               .setBranch(environment.getBranch())
               .setCredentialsProvider(credentialsProvider)
               .call();
            return tempDirectory;
        } catch (GitAPIException exception) {
            throw new ConfigurationSynchronizationException("Clone Repo Error : " + exception.getMessage());
        }
    }

}

