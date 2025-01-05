package fr.plum.e2e.manager.core.infrastructure.secondary.external.webclient.gitlab.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class GitlabResponse {

  private String id;
  private GitlabJobStatus status;
}
