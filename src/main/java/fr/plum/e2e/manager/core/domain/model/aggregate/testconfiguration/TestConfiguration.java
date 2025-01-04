package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TestConfiguration extends Entity<TestConfigurationId> {

  private TestTitle title;
  private Position position;
  private ConfigurationStatus status; // Status update by repository after test result creation
  private List<Tag> tags;
  private List<Variable> variables;
  private ZonedDateTime
      lastPlayedAt; // Last played date update by repository after test result creation

  @Builder
  public TestConfiguration(
      TestConfigurationId testConfigurationId,
      TestTitle title,
      Position position,
      ConfigurationStatus status,
      List<Tag> tags,
      List<Variable> variables,
      ZonedDateTime lastPlayedAt) {
    super(testConfigurationId);
    Assert.notNull("TestTitle", title);
    Assert.notNull("Position", position);
    Assert.notNull("ConfigurationStatus", status);
    Assert.notNull("Tags", tags);
    Assert.notNull("Variables", variables);
    this.title = title;
    this.position = position;
    this.status = status;
    this.tags = tags;
    this.variables = variables;
    this.lastPlayedAt = lastPlayedAt;
  }

  public static TestConfiguration create(
      TestTitle title, Position position, List<Tag> tags, List<Variable> variables) {
    return builder()
        .testConfigurationId(TestConfigurationId.generate())
        .title(title)
        .position(position)
        .status(ConfigurationStatus.defaultStatus())
        .tags(tags)
        .variables(variables)
        .lastPlayedAt(ZonedDateTime.now())
        .build();
  }

  public void update(TestConfiguration other) {
    this.title = other.title;
    this.position = other.position;
    this.tags = other.tags;
    this.variables = other.variables;
  }

  public boolean hasChanged(TestConfiguration other) {
    return !title.equals(other.title)
        || !status.equals(other.status)
        || !position.equals(other.position)
        || !tags.equals(other.tags)
        || !variables.equals(other.variables);
  }
}
