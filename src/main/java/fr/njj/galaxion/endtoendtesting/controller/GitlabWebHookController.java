package fr.njj.galaxion.endtoendtesting.controller;

import fr.njj.galaxion.endtoendtesting.domain.request.webhook.GitlabWebHookRequest;
import fr.njj.galaxion.endtoendtesting.service.gitlab.GitlabWebHookService;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Path("/gitlab-webhook")
@RequiredArgsConstructor
public class GitlabWebHookController {

    private final GitlabWebHookService gitlabWebHookService;

    @POST
    public void getResponses(@HeaderParam("X-Gitlab-Event") String gitlabEvent,
                             GitlabWebHookRequest request) {
        CompletableFuture.runAsync(() -> gitlabWebHookService.gitlabCallback(gitlabEvent, request));
    }

}

