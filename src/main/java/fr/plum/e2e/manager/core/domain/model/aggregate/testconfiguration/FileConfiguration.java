package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.DISABLE_TAG;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.exception.SuiteNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.TestNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.TitleDuplicationException;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AggregateRoot;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FileConfiguration extends AggregateRoot<FileName> {

  private EnvironmentId environmentId;
  private GroupName group;
  private List<SuiteConfiguration> suites;

  @Builder
  public FileConfiguration(
      FileName fileName,
      AuditInfo auditInfo,
      EnvironmentId environmentId,
      GroupName group,
      List<SuiteConfiguration> suites) {
    super(fileName, auditInfo);
    Assert.notNull("environmentId", environmentId);
    Assert.notNull("suites", suites);
    this.environmentId = environmentId;
    this.group = group;
    this.suites = suites;
  }

  public static FileConfiguration create(
      FileName fileName,
      AuditInfo auditInfo,
      EnvironmentId environmentId,
      GroupName group,
      List<SuiteConfiguration> suites) {
    return builder()
        .fileName(fileName)
        .auditInfo(auditInfo)
        .environmentId(environmentId)
        .group(group)
        .suites(suites)
        .build();
  }

  public void update(FileConfiguration newConfig) {
    this.group = newConfig.getGroup();
    updateSuites(newConfig.getSuites());
  }

  public void validateUniqueTitles() {
    var suiteTitles = new HashSet<String>();
    for (SuiteConfiguration suite : suites) {
      if (!suiteTitles.add(suite.getTitle().value())) {
        throw new TitleDuplicationException(suite.getTitle().value());
      }
      var testTitles = new HashSet<String>();
      for (TestConfiguration test : suite.getTests()) {
        if (!testTitles.add(test.getTitle().value())) {
          throw new TitleDuplicationException(test.getTitle().value());
        }
      }
    }
  }

  private void updateSuites(List<SuiteConfiguration> newSuites) {
    var updatedSuites = new ArrayList<SuiteConfiguration>();

    var existingSuitesMap =
        suites.stream()
            .collect(Collectors.toMap(suite -> suite.getTitle().value(), Function.identity()));

    for (var newSuite : newSuites) {
      var title = newSuite.getTitle().value();
      var existingSuite = existingSuitesMap.get(title);

      if (existingSuite != null) {
        existingSuite.update(newSuite);
        updateTests(existingSuite.getTests(), newSuite.getTests());
        updatedSuites.add(existingSuite);
      } else {
        updatedSuites.add(newSuite);
      }
    }

    suites = updatedSuites;
  }

  private void updateTests(
      List<TestConfiguration> existingTests, List<TestConfiguration> newTests) {
    var updatedTests = new ArrayList<TestConfiguration>();

    var existingTestsMap =
        existingTests.stream()
            .collect(Collectors.toMap(test -> test.getTitle().value(), Function.identity()));

    for (var newTest : newTests) {
      var title = newTest.getTitle().value();
      var existingTest = existingTestsMap.get(title);

      if (existingTest != null) {
        existingTest.update(newTest);
        updatedTests.add(existingTest);
      } else {
        updatedTests.add(newTest);
      }
    }

    existingTests.clear();
    existingTests.addAll(updatedTests);
  }

  public boolean hasFile(String fileName) {
    return getId().value().equals(fileName);
  }

  public boolean isEmpty() {
    return suites.isEmpty();
  }

  public void removeDisabledConfigurations() {
    List<SuiteConfiguration> modifiableSuites = new ArrayList<>(suites);

    modifiableSuites.removeIf(this::isDisabled);

    modifiableSuites.forEach(
        suiteConfiguration -> {
          List<TestConfiguration> modifiableTests = new ArrayList<>(suiteConfiguration.getTests());
          modifiableTests.removeIf(this::isDisabled);
          suiteConfiguration.updateTests(modifiableTests);
        });

    modifiableSuites.removeIf(suite -> suite.getTests().isEmpty());

    this.suites = modifiableSuites;
  }

  private boolean isDisabled(TestConfiguration test) {
    return test.getTags().stream().map(Tag::value).anyMatch(DISABLE_TAG::equals);
  }

  private boolean isDisabled(SuiteConfiguration suite) {
    return suite.getTags().stream().map(Tag::value).anyMatch(DISABLE_TAG::equals);
  }

  public boolean hasChanged(FileConfiguration newFileConfiguration) {
    if (group == null && newFileConfiguration.group != null) {
      return true;
    }
    if (group != null && !group.equals(newFileConfiguration.group)) {
      return true;
    }

    if (suites.size() != newFileConfiguration.suites.size()) {
      return true;
    }

    var thisSuites =
        suites.stream()
            .collect(Collectors.toMap(suite -> suite.getTitle().value(), Function.identity()));

    return newFileConfiguration.suites.stream()
        .anyMatch(
            otherSuite -> {
              SuiteConfiguration thisSuite = thisSuites.get(otherSuite.getTitle().value());
              return thisSuite == null || thisSuite.hasChanged(otherSuite);
            });
  }

  public boolean hasOnlyDisabledConfigurations() {
    removeDisabledConfigurations();
    return isEmpty();
  }

  public SuiteConfiguration getSuiteConfiguration(SuiteConfigurationId suiteConfigurationId) {
    return suites.stream()
        .filter(suite -> suite.getId().equals(suiteConfigurationId))
        .findFirst()
        .orElseThrow(() -> new SuiteNotFoundException(suiteConfigurationId));
  }

  public TestConfiguration getTestConfiguration(TestConfigurationId testConfigurationId) {
    return suites.stream()
        .flatMap(suite -> suite.getTests().stream())
        .filter(test -> test.getId().equals(testConfigurationId))
        .findFirst()
        .orElseThrow(() -> new TestNotFoundException(testConfigurationId));
  }
}
