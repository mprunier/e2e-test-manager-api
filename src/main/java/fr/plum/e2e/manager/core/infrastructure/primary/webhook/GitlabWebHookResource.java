package fr.plum.e2e.manager.core.infrastructure.primary.webhook;

import fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto.request.GitlabWebHookRequest;
import fr.plum.e2e.manager.sharedkernel.domain.exception.CustomException;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("/gitlab-webhook")
@RequiredArgsConstructor
public class GitlabWebHookResource {

  private final GitlabWebHookHandler gitlabWebHookHandler;

  @POST
  public void getResponses(
      @HeaderParam("X-Gitlab-Event") String gitlabEvent, GitlabWebHookRequest request) {
    CompletableFuture.runAsync(
        () -> {
          try {
            gitlabWebHookHandler.gitlabCallback(gitlabEvent, request);
          } catch (CustomException e) {
            log.debug(
                "Business Webhook Async Error : {}",
                e.getDescription()); // Hook likely coming from another configured branch or
            // similar.
            // So not important.
          } catch (Exception e) {
            log.error("Webhook Async Error : {}", e.getMessage());
          }
        });
  }
}
