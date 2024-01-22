package fr.njj.galaxion.endtoendtesting.client.gitlab.response;

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
public class GitlabTreeResponse {

  private String id;
  private String type;
  private String path;
  private String name;
}
