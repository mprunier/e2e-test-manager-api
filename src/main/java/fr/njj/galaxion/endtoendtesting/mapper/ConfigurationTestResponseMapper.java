package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.internal.InProgressPipelinesInternal;
import fr.njj.galaxion.endtoendtesting.domain.internal.PipelineDetailsInternal;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteOrTestPipelineResponse;
import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestTagEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationTestResponseMapper {

  public static ConfigurationTestResponse build(
      ConfigurationTestEntity entity, InProgressPipelinesInternal inProgressPipelines) {
    var variables = new HashSet<String>();
    if (entity.getVariables() != null) {
      variables.addAll(entity.getVariables());
    }
    if (entity.getConfigurationSuite().getVariables() != null) {
      variables.addAll(entity.getConfigurationSuite().getVariables());
    }
    var pipelinesInProgress = getPipelinesInProgress(entity, inProgressPipelines);
    var configurationTestResponseBuilder =
        ConfigurationTestResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .status(entity.getStatus())
            .variables(variables)
            .pipelinesInProgress(pipelinesInProgress)
            .tags(
                entity.getConfigurationTags() != null
                    ? entity.getConfigurationTags().stream()
                        .map(ConfigurationTestTagEntity::getTag)
                        .toList()
                    : null)
            .lastPlayedAt(entity.getLastPlayedAt());

    return configurationTestResponseBuilder.build();
  }

  private static List<ConfigurationSuiteOrTestPipelineResponse> getPipelinesInProgress(
      ConfigurationTestEntity configurationTest, InProgressPipelinesInternal inProgressPipelines) {
    var pipelinesInProgress = new ArrayList<ConfigurationSuiteOrTestPipelineResponse>();
    if (inProgressPipelines.isAllTests()) {
      pipelinesInProgress.add(
          ConfigurationSuiteOrTestPipelineResponse.builder().isAllTests(true).build());
    }
    for (var entry : inProgressPipelines.pipelinesByConfigurationTestId().entrySet()) {
      if (configurationTest.getId().equals(entry.getKey())) {
        List<PipelineDetailsInternal> pipelineDetails = entry.getValue();
        pipelineDetails.forEach(
            pipelineDetail ->
                pipelinesInProgress.add(
                    ConfigurationSuiteOrTestPipelineResponse.builder()
                        .id(pipelineDetail.id())
                        .createdAt(pipelineDetail.createdAt())
                        .createdBy(pipelineDetail.createdBy())
                        .build()));
      }
    }
    return pipelinesInProgress;
  }

  public static List<ConfigurationTestResponse> builds(
      List<ConfigurationTestEntity> entities, InProgressPipelinesInternal inProgressPipelines) {
    return entities.stream().map(entity -> build(entity, inProgressPipelines)).toList();
  }

  public static ConfigurationTestResponse buildTitle(ConfigurationTestEntity entity) {
    return ConfigurationTestResponse.builder().id(entity.getId()).title(entity.getTitle()).build();
  }

  public static List<ConfigurationTestResponse> buildTitles(
      List<ConfigurationTestEntity> entities) {
    return entities.stream().map(ConfigurationTestResponseMapper::buildTitle).toList();
  }
}
