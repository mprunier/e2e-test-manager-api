package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationTestResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestEntity;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestTagEntity;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationTestResponseMapper {

  public static ConfigurationTestResponse build(ConfigurationTestEntity entity) {
    var variables = new HashSet<String>();
    if (entity.getVariables() != null) {
      variables.addAll(entity.getVariables());
    }
    if (entity.getConfigurationSuite().getVariables() != null) {
      variables.addAll(entity.getConfigurationSuite().getVariables());
    }
    var configurationTestResponseBuilder =
        ConfigurationTestResponse.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .status(entity.getStatus())
            .variables(variables)
            .tags(
                entity.getConfigurationTags() != null
                    ? entity.getConfigurationTags().stream()
                        .map(ConfigurationTestTagEntity::getTag)
                        .toList()
                    : null)
            .lastPlayedAt(entity.getLastPlayedAt());

    return configurationTestResponseBuilder.build();
  }

  public static List<ConfigurationTestResponse> builds(List<ConfigurationTestEntity> entities) {
    return entities.stream().map(ConfigurationTestResponseMapper::build).toList();
  }

  public static ConfigurationTestResponse buildTitle(ConfigurationTestEntity entity) {
    return ConfigurationTestResponse.builder().id(entity.getId()).title(entity.getTitle()).build();
  }

  public static List<ConfigurationTestResponse> buildTitles(
      List<ConfigurationTestEntity> entities) {
    return entities.stream().map(ConfigurationTestResponseMapper::buildTitle).toList();
  }
}
