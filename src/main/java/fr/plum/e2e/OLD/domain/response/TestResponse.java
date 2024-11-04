package fr.plum.e2e.OLD.domain.response;

import fr.plum.e2e.OLD.domain.enumeration.ConfigurationStatus;
import java.time.ZonedDateTime;
import java.util.List;
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
public class TestResponse {

  private Long id;

  private Long configurationId;

  private String configurationFileTitle;

  private String configurationSuiteTitle;

  private String configurationTestTitle;

  private ConfigurationStatus status;

  private String reference;

  private Integer onProgressPercentage;

  private ZonedDateTime createdAt;

  @Setter @ToString.Exclude private List<ScreenshotResponse> screenshots;

  @Setter private Boolean hasVideo;

  @Setter private String errorUrl;

  @Setter private String errorMessage;

  @Setter @ToString.Exclude private String errorStacktrace;

  @Setter @ToString.Exclude private String code;

  @Setter private Integer duration;

  private String createdBy;

  @Setter @ToString.Exclude private List<TestVariableResponse> variables;

  @Setter @ToString.Exclude private List<String> configurationTestTags;
}
