package fr.njj.galaxion.endtoendtesting.domain.request.webhook;

import java.util.List;
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
public class GitlabWebHookCommitRequest {

  private List<String> added;
  private List<String> modified;
  private List<String> removed;
}
