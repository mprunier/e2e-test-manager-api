package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.webhook.GitlabWebHookRequest;
import fr.njj.galaxion.endtoendtesting.lib.exception.CustomException;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabWebHookService;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/gitlab-webhook")
@RequiredArgsConstructor
public class GitlabWebHookController {

  private final GitlabWebHookService gitlabWebHookService;

  @POST
  public void getResponses(
      @HeaderParam("X-Gitlab-Event") String gitlabEvent, GitlabWebHookRequest request) {
    CompletableFuture.runAsync(
        () -> {
          try {
            gitlabWebHookService.gitlabCallback(gitlabEvent, request);
          } catch (CustomException e) {
            log.trace(
                "Business Webhook Async Error : {}",
                e.getDetail()); // Hook likely coming from another configured branch or similar.
            // So not important.
          } catch (Exception e) {
            log.error("Webhook Async Error : {}", e.getMessage());
          }
        });
  }
}
