package fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto;

import fr.plum.e2e.manager.core.infrastructure.primary.webhook.exception.GitlabWebHookEventNotExistException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GitLabWebhookEvent {
  PUSH_HOOK("Push Hook"),
  //    TAG_PUSH_HOOK("Tag Push Hook"),
  //    ISSUE_HOOK("Issue Hook"),
  //    NOTE_HOOK("Note Hook"),
  //    MERGE_REQUEST_HOOK("Merge Request Hook"),
  //    WIKI_PAGE_HOOK("Wiki Page Hook"),
  //    PIPELINE_HOOK("Pipeline Hook"),
  JOB_HOOK("Job Hook");

  private final String headerValue;

  public static GitLabWebhookEvent fromHeaderValue(String headerValue) {
    for (GitLabWebhookEvent event : values()) {
      if (event.getHeaderValue().equals(headerValue)) {
        return event;
      }
    }
    throw new GitlabWebHookEventNotExistException(headerValue);
  }
}
