package fr.njj.galaxion.endtoendtesting.mapper;

import fr.njj.galaxion.endtoendtesting.domain.response.ConfigurationIdentifierResponse;
import fr.njj.galaxion.endtoendtesting.model.entity.ConfigurationTestIdentifierEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationIdentifierResponseMapper {

    public static ConfigurationIdentifierResponse build(ConfigurationTestIdentifierEntity entity) {
        var variables = new HashSet<String>();
        if (entity.getConfigurationTest().getVariables() != null) {
            variables.addAll(entity.getConfigurationTest().getVariables());
        }
        if (entity.getConfigurationTest().getConfigurationSuite().getVariables() != null) {
            variables.addAll(entity.getConfigurationTest().getConfigurationSuite().getVariables());
        }
        return ConfigurationIdentifierResponse.builder()
                                              .id(entity.getId())
                                              .identifier(entity.getIdentifier())
                                              .testId(entity.getConfigurationTest().getId())
                                              .status(entity.getConfigurationTest().getStatus())
                                              .testTitle(entity.getConfigurationTest().getTitle())
                                              .suiteTitle(entity.getConfigurationTest().getConfigurationSuite().getTitle())
                                              .path(entity.getConfigurationTest().getFile())
                                              .variables(variables)
                                              .lastPlayedAt(entity.getConfigurationTest().getLastPlayedAt())
                                              .build();
    }

    public static List<ConfigurationIdentifierResponse> builds(List<ConfigurationTestIdentifierEntity> entities) {
        return entities.stream()
                       .map(ConfigurationIdentifierResponseMapper::build)
                       .toList();
    }
}
