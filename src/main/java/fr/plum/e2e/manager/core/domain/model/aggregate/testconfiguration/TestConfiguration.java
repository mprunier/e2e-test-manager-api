package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.Entity;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class TestConfiguration extends Entity<TestConfigurationId> {

  private TestTitle title;
  private Position position;
  @Builder.Default private ConfigurationStatus status = ConfigurationStatus.defaultStatus();
  @Builder.Default private List<Tag> tags = new ArrayList<>();
  @Builder.Default private List<Variable> variables = new ArrayList<>();
  private ZonedDateTime lastPlayedAt;

  public void initializeId() {
    this.id = TestConfigurationId.generate();
  }

  public void updateFrom(TestConfiguration other) {
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
