package fr.plum.e2e.OLD.domain.response;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
public class ConfigurationSuiteResponse {

  private Long id;

  private String title;

  private String file;

  private ConfigurationStatus status;

  @ToString.Exclude private List<String> variables;

  private List<String> tags;

  @Builder.Default private List<ConfigurationTestResponse> tests = new ArrayList<>();

  private ZonedDateTime lastPlayedAt;

  private boolean hasNewTest;

  private List<ConfigurationSuiteOrTestPipelineResponse> pipelinesInProgress;

  private String group;
}
