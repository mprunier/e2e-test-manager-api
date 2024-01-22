package fr.njj.galaxion.endtoendtesting.client.gitlab.request;

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
public class VariableRequest {

  private String key;
  private String value;
}
