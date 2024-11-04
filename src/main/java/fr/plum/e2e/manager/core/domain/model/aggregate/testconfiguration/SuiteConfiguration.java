package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.shared.Entity;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class SuiteConfiguration extends Entity<SuiteConfigurationId> {

  private SuiteTitle title;
  @Builder.Default private ConfigurationStatus status = ConfigurationStatus.defaultStatus();
  @Builder.Default private List<TestConfiguration> tests = new ArrayList<>();
  @Builder.Default private List<Tag> tags = new ArrayList<>();
  @Builder.Default private List<Variable> variables = new ArrayList<>();
  private ZonedDateTime lastPlayedAt;

  public void initializeId() {
    this.id = SuiteConfigurationId.generate();
    tests.forEach(TestConfiguration::initializeId);
  }

  public void updateFrom(SuiteConfiguration other) {
    this.title = other.title;
    this.tags = other.tags;
    this.variables = other.variables;
  }

  public boolean hasChanged(SuiteConfiguration other) {
    if (!title.equals(other.title)
        || !status.equals(other.status)
        || !tags.equals(other.tags)
        || !variables.equals(other.variables)) {
      return true;
    }

    if (tests.size() != other.tests.size()) {
      return true;
    }

    var thisTests =
        tests.stream()
            .collect(Collectors.toMap(test -> test.getTitle().value(), Function.identity()));

    return other.tests.stream()
        .anyMatch(
            otherTest -> {
              TestConfiguration thisTest = thisTests.get(otherTest.getTitle().value());
              return thisTest == null || thisTest.hasChanged(otherTest);
            });
  }
}
