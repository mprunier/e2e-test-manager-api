package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationSuiteProjection;
import fr.plum.e2e.manager.core.domain.model.projection.ConfigurationTestProjection;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaTestConfigurationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SuiteMapper {

  public static ConfigurationSuiteProjection toSuiteResponse(JpaSuiteConfigurationEntity entity) {
    return ConfigurationSuiteProjection.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .file(entity.getFileConfiguration().getFileName())
        .status(entity.getStatus())
        .variables(entity.getVariables())
        .tags(entity.getTags())
        .tests(entity.getTestConfigurations().stream().map(SuiteMapper::toTestResponse).toList())
        .lastPlayedAt(entity.getLastPlayedAt())
        .hasNewTest(
            entity.getTestConfigurations().stream()
                .anyMatch(test -> test.getStatus() == ConfigurationStatus.NEW))
        .group(entity.getFileConfiguration().getGroupName())
        .build();
  }

  private static ConfigurationTestProjection toTestResponse(JpaTestConfigurationEntity test) {
    return ConfigurationTestProjection.builder()
        .id(test.getId())
        .title(test.getTitle())
        .status(test.getStatus())
        .variables(test.getVariables())
        .tags(test.getTags())
        .lastPlayedAt(test.getLastPlayedAt())
        .build();
  }
}
