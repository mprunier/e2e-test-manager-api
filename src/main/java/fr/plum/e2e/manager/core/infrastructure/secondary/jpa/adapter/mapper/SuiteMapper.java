package fr.plum.e2e.manager.core.infrastructure.secondary.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.ConfigurationStatus;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationSuiteView;
import fr.plum.e2e.manager.core.domain.model.view.ConfigurationTestView;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.jpa.entity.testconfiguration.JpaTestConfigurationEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SuiteMapper {

  public static ConfigurationSuiteView toSuiteResponse(JpaSuiteConfigurationEntity entity) {
    return ConfigurationSuiteView.builder()
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

  private static ConfigurationTestView toTestResponse(JpaTestConfigurationEntity test) {
    return ConfigurationTestView.builder()
        .id(test.getId())
        .title(test.getTitle())
        .status(test.getStatus())
        .variables(test.getVariables())
        .tags(test.getTags())
        .lastPlayedAt(test.getLastPlayedAt())
        .build();
  }
}
