package fr.njj.galaxion.endtoendtesting.domain.response;

import java.time.ZonedDateTime;
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
public class EnvironmentResponse {

  private Long id;
  private String description;
  private String branch;
  private String projectId;
  private String token;
  private String createdBy;
  private String updatedBy;
  private Boolean isEnabled;
  @ToString.Exclude private List<EnvironmentVariableResponse> variables;
  private ZonedDateTime createdAt;
  private ZonedDateTime updatedAt;
  private Boolean isLocked;
  private Boolean isRunningAllTests;
  private String lastAllTestsError;
}
