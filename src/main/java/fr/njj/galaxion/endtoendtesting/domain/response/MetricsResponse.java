package fr.njj.galaxion.endtoendtesting.domain.response;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class MetricsResponse {

  private ZonedDateTime at;

  private Integer suites;

  private Integer tests;

  private Integer passes;

  private Integer failures;

  private Integer skipped;

  private Integer passPercent;

  private boolean isAllTestsRun;

  @Setter private ZonedDateTime lastAllTestsRunAt;
}
