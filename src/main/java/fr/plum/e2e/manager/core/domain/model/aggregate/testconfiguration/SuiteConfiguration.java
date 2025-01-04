package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SuiteConfiguration extends Entity<SuiteConfigurationId> {

  private SuiteTitle title;
  private ConfigurationStatus status; // Status update by repository after test result creation
  private List<TestConfiguration> tests;
  private List<Tag> tags;
  private List<Variable> variables;
  private ZonedDateTime
      lastPlayedAt; // Last played date update by repository after test result creation

  @Builder
  public SuiteConfiguration(
      SuiteConfigurationId suiteConfigurationId,
      SuiteTitle title,
      ConfigurationStatus status,
      List<TestConfiguration> tests,
      List<Tag> tags,
      List<Variable> variables,
      ZonedDateTime lastPlayedAt) {
    super(suiteConfigurationId);
    Assert.notNull("title", title);
    Assert.notNull("status", status);
    Assert.notNull("tests", tests);
    Assert.notNull("tags", tags);
    Assert.notNull("variables", variables);
    this.title = title;
    this.status = status;
    this.tests = tests;
    this.tags = tags;
    this.variables = variables;
    this.lastPlayedAt = lastPlayedAt;
  }

  public static SuiteConfiguration create(
      SuiteTitle title, List<Tag> tags, List<Variable> variables, List<TestConfiguration> tests) {
    return builder()
        .suiteConfigurationId(SuiteConfigurationId.generate())
        .title(title)
        .status(ConfigurationStatus.defaultStatus())
        .tests(tests)
        .tags(tags)
        .variables(variables)
        .lastPlayedAt(ZonedDateTime.now())
        .build();
  }

  public void update(SuiteConfiguration newSuiteConfiguration) {
    this.title = newSuiteConfiguration.title;
    this.tags = newSuiteConfiguration.tags;
    this.variables = newSuiteConfiguration.variables;
  }

  public boolean hasChanged(SuiteConfiguration newSuiteConfiguration) {
    if (!title.equals(newSuiteConfiguration.title)
        || !status.equals(newSuiteConfiguration.status)
        || !tags.equals(newSuiteConfiguration.tags)
        || !variables.equals(newSuiteConfiguration.variables)) {
      return true;
    }

    if (tests.size() != newSuiteConfiguration.tests.size()) {
      return true;
    }

    var thisTests =
        tests.stream()
            .collect(Collectors.toMap(test -> test.getTitle().value(), Function.identity()));

    return newSuiteConfiguration.tests.stream()
        .anyMatch(
            otherTest -> {
              TestConfiguration thisTest = thisTests.get(otherTest.getTitle().value());
              return thisTest == null || thisTest.hasChanged(otherTest);
            });
  }
}
