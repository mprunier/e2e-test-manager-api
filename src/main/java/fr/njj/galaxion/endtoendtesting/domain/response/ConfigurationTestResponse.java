package fr.njj.galaxion.endtoendtesting.domain.response;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
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
public class ConfigurationTestResponse {

  private Long id;

  private String title;

  private ConfigurationStatus status;

  private Long suiteId;

  private String suiteTitle;

  private String path;

  @ToString.Exclude private Set<String> variables;

  private List<String> tags;

  private ZonedDateTime lastPlayedAt;

  private int pipelineInProgress;
}
