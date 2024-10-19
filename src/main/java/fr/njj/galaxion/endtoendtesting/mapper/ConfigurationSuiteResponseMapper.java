package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressTestInternal;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationSuiteResponseMapper {

  public static ConfigurationSuiteResponse build(
      ConfigurationSuiteEntity entity, InProgressTestInternal inProgressTests) {
    var tests =
        ConfigurationTestResponseMapper.builds(entity.getConfigurationTests(), inProgressTests);
    var hasNewTest =
        tests.stream().anyMatch(test -> ConfigurationStatus.NEW.equals(test.getStatus()));
    var pipelineInProgress = getPipelineInProgress(tests);
    return ConfigurationSuiteResponse.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .file(entity.getFile())
        .status(entity.getStatus())
        .variables(entity.getVariables())
        .tests(tests)
        .pipelineInProgress(pipelineInProgress)
        .lastPlayedAt(entity.getLastPlayedAt())
        .hasNewTest(hasNewTest)
        .build();
  }

  private static int getPipelineInProgress(List<ConfigurationTestResponse> tests) {
    var pipelineInProgress = 0;
    for (ConfigurationTestResponse test : tests) {
      pipelineInProgress = Math.max(pipelineInProgress, test.getPipelineInProgress());
    }
    return pipelineInProgress;
  }

  public static List<ConfigurationSuiteResponse> builds(
      List<ConfigurationSuiteEntity> entities, InProgressTestInternal inProgressTests) {
    return entities.stream().map(entity -> build(entity, inProgressTests)).toList();
  }

  public static ConfigurationSuiteResponse buildTitle(ConfigurationSuiteEntity entity) {
    return ConfigurationSuiteResponse.builder().id(entity.getId()).title(entity.getTitle()).build();
  }

  public static List<ConfigurationSuiteResponse> buildTitles(
      List<ConfigurationSuiteEntity> entities) {
    return entities.stream().map(ConfigurationSuiteResponseMapper::buildTitle).toList();
  }
}
