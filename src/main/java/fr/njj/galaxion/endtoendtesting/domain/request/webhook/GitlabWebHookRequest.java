package fr.njj.galaxion.endtoendtesting.domain.request.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GitlabWebHookRequest {

    private String ref;

    @JsonProperty("project_id")
    private String projectId;

    @JsonProperty("pipeline_id")
    private String pipelineId;

    @JsonProperty("build_id")
    private String jobId;

    @JsonProperty("build_finished_at")
    private String finishedAt;

    @JsonProperty("build_status")
    private String status;

    private List<GitlabWebHookCommitRequest> commits;
}
