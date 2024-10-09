package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.enumeration.ConfigurationStatus;
import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressPipelinesInternal;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteOrTestPipelineResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteTagEntity;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationSuiteResponseMapper {

  public static ConfigurationSuiteResponse build(
      ConfigurationSuiteEntity entity,
      InProgressPipelinesInternal inProgressPipelines,
      Map<String, String> fileByGroupMap) {
    var tests =
        ConfigurationTestResponseMapper.builds(entity.getConfigurationTests(), inProgressPipelines);
    var hasNewTest =
        tests.stream().anyMatch(test -> ConfigurationStatus.NEW.equals(test.getStatus()));
    var pipelinesInProgress = getPipelinesInProgress(tests);
    return ConfigurationSuiteResponse.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .file(entity.getFile())
        .status(entity.getStatus())
        .variables(entity.getVariables())
        .tags(
            entity.getConfigurationTags() != null
                ? entity.getConfigurationTags().stream()
                    .map(ConfigurationSuiteTagEntity::getTag)
                    .toList()
                : null)
        .tests(tests)
        .pipelinesInProgress(pipelinesInProgress)
        .lastPlayedAt(entity.getLastPlayedAt())
        .hasNewTest(hasNewTest)
        .group(fileByGroupMap.get(entity.getFile()))
        .build();
  }

  private static List<ConfigurationSuiteOrTestPipelineResponse> getPipelinesInProgress(
      List<ConfigurationTestResponse> tests) {
    return tests.stream()
        .flatMap(test -> test.getPipelinesInProgress().stream())
        .distinct()
        .collect(Collectors.toList());
  }

  public static List<ConfigurationSuiteResponse> builds(
      List<ConfigurationSuiteEntity> entities,
      InProgressPipelinesInternal inProgressPipelines,
      Map<String, String> fileByGroupMap) {
    return entities.stream()
        .map(entity -> build(entity, inProgressPipelines, fileByGroupMap))
        .toList();
  }

  public static ConfigurationSuiteResponse buildTitle(ConfigurationSuiteEntity entity) {
    return ConfigurationSuiteResponse.builder().id(entity.getId()).title(entity.getTitle()).build();
  }

  public static List<ConfigurationSuiteResponse> buildTitles(
      List<ConfigurationSuiteEntity> entities) {
    return entities.stream().map(ConfigurationSuiteResponseMapper::buildTitle).toList();
  }
}
