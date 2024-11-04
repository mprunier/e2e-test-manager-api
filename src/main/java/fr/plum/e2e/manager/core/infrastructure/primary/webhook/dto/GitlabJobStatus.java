package fr.plum.e2e.manager.core.infrastructure.primary.webhook.dto;

import fr.plum.e2e.manager.core.infrastructure.primary.webhook.exception.GitlabStatusNotExistException;

public enum GitlabJobStatus {
  created,
  pending,
  running,
  success,
  failed,
  canceled,
  skipped;

  public static GitlabJobStatus fromHeaderValue(String headerValue) {
    for (GitlabJobStatus status : values()) {
      if (status.name().equals(headerValue)) {
        return status;
      }
    }
    throw new GitlabStatusNotExistException(headerValue);
  }
}
