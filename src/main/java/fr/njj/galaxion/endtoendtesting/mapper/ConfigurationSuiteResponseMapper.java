package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationSuiteResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationSuiteEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationSuiteResponseMapper {

    public static ConfigurationSuiteResponse build(ConfigurationSuiteEntity entity) {
        var tests = ConfigurationTestResponseMapper.builds(entity.getConfigurationTests(), false);
        return ConfigurationSuiteResponse.builder()
                                         .id(entity.getId())
                                         .title(entity.getTitle())
                                         .file(entity.getFile())
                                         .status(entity.getStatus())
                                         .variables(entity.getVariables())
                                         .tests(tests)
                                         .lastPlayedAt(entity.getLastPlayedAt())
                                         .build();
    }

    public static List<ConfigurationSuiteResponse> builds(List<ConfigurationSuiteEntity> entities) {
        return entities.stream()
                       .map(ConfigurationSuiteResponseMapper::build)
                       .toList();
    }

    public static ConfigurationSuiteResponse buildTitle(ConfigurationSuiteEntity entity) {
        return ConfigurationSuiteResponse
                .builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .build();
    }

    public static List<ConfigurationSuiteResponse> buildTitles(List<ConfigurationSuiteEntity> entities) {
        return entities.stream()
                       .map(ConfigurationSuiteResponseMapper::buildTitle)
                       .toList();
    }
}
