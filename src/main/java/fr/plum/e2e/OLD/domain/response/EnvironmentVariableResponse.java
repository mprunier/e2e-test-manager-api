package fr.plum.e2e.OLD.domain.response;

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
public class EnvironmentVariableResponse {

  private Long id;
  private String name;
  private String defaultValue;
  private String description;
  private Boolean isHidden;
}
