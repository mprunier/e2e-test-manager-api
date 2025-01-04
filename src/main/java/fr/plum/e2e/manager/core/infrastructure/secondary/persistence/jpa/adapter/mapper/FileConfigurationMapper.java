package fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.adapter.mapper;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.FileConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.SuiteConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.TestConfiguration;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaFileConfigurationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaSuiteConfigurationEntity;
import fr.plum.e2e.manager.core.infrastructure.secondary.persistence.jpa.entity.testconfiguration.JpaTestConfigurationEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileConfigurationMapper {

  public static FileConfiguration toDomain(JpaFileConfigurationEntity entity) {
    return FileConfiguration.builder()
        .fileName(new FileName(entity.getFileName()))
        .environmentId(new EnvironmentId(entity.getEnvironmentId()))
        .group(new GroupName(entity.getGroupName()))
        .suites(
            entity.getSuiteConfigurations().stream()
                .map(FileConfigurationMapper::toSuiteDomain)
                .toList())
        .build();
  }

  public static List<FileConfiguration> toDomainList(List<JpaFileConfigurationEntity> entities) {
    return entities.stream().map(FileConfigurationMapper::toDomain).collect(Collectors.toList());
  }

  public static JpaFileConfigurationEntity toEntity(FileConfiguration domain) {
    var entity =
        JpaFileConfigurationEntity.builder()
            .fileName(domain.getId().value())
            .environmentId(domain.getEnvironmentId().value())
            .groupName(domain.getGroup() != null ? domain.getGroup().value() : null)
            .suiteConfigurations(new ArrayList<>())
            .build();

    var suiteEntities =
        domain.getSuites().stream().map(suite -> toSuiteEntity(suite, entity)).toList();

    entity.getSuiteConfigurations().addAll(suiteEntities);

    return entity;
  }

  public static SuiteConfiguration toSuiteDomain(JpaSuiteConfigurationEntity entity) {
    return SuiteConfiguration.builder()
        .suiteConfigurationId(new SuiteConfigurationId(entity.getId()))
        .title(new SuiteTitle(entity.getTitle()))
        .status(entity.getStatus())
        .lastPlayedAt(entity.getLastPlayedAt())
        .tags(entity.getTags().stream().map(Tag::new).collect(Collectors.toList()))
        .variables(entity.getVariables().stream().map(Variable::new).collect(Collectors.toList()))
        .tests(
            entity.getTestConfigurations().stream()
                .map(FileConfigurationMapper::toTestDomain)
                .collect(Collectors.toList()))
        .build();
  }

  private static JpaSuiteConfigurationEntity toSuiteEntity(
      SuiteConfiguration domain, JpaFileConfigurationEntity fileConfig) {
    var entity =
        JpaSuiteConfigurationEntity.builder()
            .id(domain.getId().value())
            .title(domain.getTitle().value())
            .status(domain.getStatus())
            .lastPlayedAt(domain.getLastPlayedAt())
            .tags(domain.getTags().stream().map(Tag::value).collect(Collectors.toList()))
            .variables(
                domain.getVariables().stream().map(Variable::value).collect(Collectors.toList()))
            .testConfigurations(new ArrayList<>())
            .fileConfiguration(fileConfig)
            .build();

    var testEntities = domain.getTests().stream().map(test -> toTestEntity(test, entity)).toList();

    entity.getTestConfigurations().addAll(testEntities);

    return entity;
  }

  private static TestConfiguration toTestDomain(JpaTestConfigurationEntity entity) {
    return TestConfiguration.builder()
        .testConfigurationId(new TestConfigurationId(entity.getId()))
        .title(new TestTitle(entity.getTitle()))
        .status(entity.getStatus())
        .position(new Position(entity.getPosition()))
        .lastPlayedAt(entity.getLastPlayedAt())
        .tags(entity.getTags().stream().map(Tag::new).collect(Collectors.toList()))
        .variables(entity.getVariables().stream().map(Variable::new).collect(Collectors.toList()))
        .build();
  }

  private static JpaTestConfigurationEntity toTestEntity(
      TestConfiguration domain, JpaSuiteConfigurationEntity suiteConfig) {
    return JpaTestConfigurationEntity.builder()
        .id(domain.getId().value())
        .title(domain.getTitle().value())
        .status(domain.getStatus())
        .position(domain.getPosition().value())
        .lastPlayedAt(domain.getLastPlayedAt())
        .tags(domain.getTags().stream().map(Tag::value).collect(Collectors.toList()))
        .variables(domain.getVariables().stream().map(Variable::value).collect(Collectors.toList()))
        .suiteConfiguration(suiteConfig)
        .build();
  }

  public static Map<GroupName, List<FileName>> toDomainFileNamesMapByGroupName(
      List<JpaFileConfigurationEntity> fileConfigurationEntities) {
    return fileConfigurationEntities.stream()
        .collect(
            Collectors.groupingBy(
                entity -> new GroupName(entity.getGroupName()),
                Collectors.mapping(
                    entity -> new FileName(entity.getFileName()), Collectors.toList())));
  }

  public static FileName toDomainFileName(JpaFileConfigurationEntity jpaFileConfigurationEntity) {
    return new FileName(jpaFileConfigurationEntity.getFileName());
  }
}
