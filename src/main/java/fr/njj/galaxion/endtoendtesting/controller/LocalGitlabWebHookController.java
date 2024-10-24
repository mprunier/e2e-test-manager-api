package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.webhook.GitlabWebHookRequest;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("")
@RequiredArgsConstructor
public class LocalGitlabWebHookController {

  private final GitlabWebHookController gitlabWebHookController;

  @POST
  public void getResponses(
      @HeaderParam("X-Gitlab-Event") String gitlabEvent, GitlabWebHookRequest request) {
    log.debug("Webhook received : {} - {}", gitlabEvent, request.getStatus());
    gitlabWebHookController.getResponses(gitlabEvent, request);
  }
}
