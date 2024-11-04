package fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record GitlabWebHookRequest(
    String ref,
    @JsonProperty("project_id") String projectId,
    @JsonProperty("pipeline_id") String pipelineId,
    @JsonProperty("build_id") String jobId,
    @JsonProperty("build_finished_at") String finishedAt,
    @JsonProperty("build_status") String status,
    List<GitlabWebHookCommitRequest> commits) {}
