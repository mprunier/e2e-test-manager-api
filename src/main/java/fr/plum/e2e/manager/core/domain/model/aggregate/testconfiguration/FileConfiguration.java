package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.DISABLE_TAG;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.shared.AggregateRoot;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.exception.SuiteNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.TestNotFoundException;
import fr.plum.e2e.manager.core.domain.model.exception.TitleDuplicationException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class FileConfiguration extends AggregateRoot<FileName> {

  @Setter private EnvironmentId environmentId;
  private GroupName group;
  @Builder.Default private List<SuiteConfiguration> suites = new ArrayList<>();

  public void initializeSuitesAndTestsIds() {
    suites.forEach(SuiteConfiguration::initializeId);
  }

  public void updateFrom(FileConfiguration newConfig) {
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
        existingSuite.updateFrom(newSuite);
        updateTests(existingSuite.getTests(), newSuite.getTests());
        updatedSuites.add(existingSuite);
      } else {
        newSuite.initializeId();
        newSuite.getTests().forEach(TestConfiguration::initializeId);
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
        existingTest.updateFrom(newTest);
        updatedTests.add(existingTest);
      } else {
        newTest.initializeId();
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
    suites.removeIf(this::isDisabled);

    suites.forEach(suiteConfiguration -> suiteConfiguration.getTests().removeIf(this::isDisabled));
    suites.removeIf(suite -> suite.getTests().isEmpty());
  }

  private boolean isDisabled(TestConfiguration test) {
    return test.getTags().stream().map(Tag::value).anyMatch(DISABLE_TAG::equals);
  }

  private boolean isDisabled(SuiteConfiguration suite) {
    return suite.getTags().stream().map(Tag::value).anyMatch(DISABLE_TAG::equals);
  }

  public boolean hasChanged(FileConfiguration other) {
    if (group == null && other.group != null) {
      return true;
    }
    if (group != null && !group.equals(other.group)) {
      return true;
    }

    if (suites.size() != other.suites.size()) {
      return true;
    }

    var thisSuites =
        suites.stream()
            .collect(Collectors.toMap(suite -> suite.getTitle().value(), Function.identity()));

    return other.suites.stream()
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
