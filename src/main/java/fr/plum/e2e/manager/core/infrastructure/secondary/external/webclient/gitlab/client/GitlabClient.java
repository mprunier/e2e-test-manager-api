package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.client;

import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.exception.GitlabExceptionMapper;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.request.PipelineRequest;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.response.GitlabProjectDetail;
import fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.response.GitlabResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "gitlab")
@RegisterProvider(GitlabExceptionMapper.class)
public interface GitlabClient {

  @POST
  @Path("/projects/{project_id}/pipeline")
  GitlabResponse runPipeline(
      @HeaderParam("PRIVATE-TOKEN") String token,
      @PathParam("project_id") String projectId,
      PipelineRequest request);

  @GET
  @Path("/projects/{project_id}/pipelines/{pipeline_id}/jobs")
  List<GitlabResponse> getJobs(
      @HeaderParam("PRIVATE-TOKEN") String token,
      @PathParam("project_id") String projectId,
      @PathParam("pipeline_id") String pipelineId);

  @POST
  @Path("/projects/{project_id}/pipelines/{pipeline_id}/cancel")
  void cancelPipeline(
      @HeaderParam("PRIVATE-TOKEN") String token,
      @PathParam("project_id") String projectId,
      @PathParam("pipeline_id") String pipelineId);

  @GET
  @Path("/projects/{project_id}/jobs/{job_id}/artifacts")
  Response getJobArtifacts(
      @HeaderParam("PRIVATE-TOKEN") String token,
      @PathParam("project_id") String projectId,
      @PathParam("job_id") String jobId);

  @GET
  @Path("/projects/{project_id}")
  GitlabProjectDetail getProjectDetail(
      @HeaderParam("PRIVATE-TOKEN") String token, @PathParam("project_id") String projectId);
}
