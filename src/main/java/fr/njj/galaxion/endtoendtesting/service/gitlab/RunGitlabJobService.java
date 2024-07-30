package fr.njj.galaxion.endtoendtesting.service.gitlab;

import fr.njj.galaxion.endtoendtesting.client.gitlab.GitlabClient;
import fr.njj.galaxion.endtoendtesting.client.gitlab.request.PipelineRequest;
import fr.njj.galaxion.endtoendtesting.client.gitlab.request.VariableRequest;
import fr.njj.galaxion.endtoendtesting.client.gitlab.response.GitlabResponse;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import static fr.njj.galaxion.endtoendtesting.domain.constant.CommonConstant.START_PATH;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class RunGitlabJobService {

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
}

