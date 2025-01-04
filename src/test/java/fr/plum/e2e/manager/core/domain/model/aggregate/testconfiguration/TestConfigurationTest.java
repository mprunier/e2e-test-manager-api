package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TestConfigurationTest {

  private TestTitle title;
  private Position position;
  private ConfigurationStatus status;
  private List<Tag> tags;
  private List<Variable> variables;
  private ZonedDateTime lastPlayedAt;

  @BeforeEach
  void setUp() {
    title = new TestTitle("Test Case");
    position = new Position(1);
    status = ConfigurationStatus.NEW;
    tags = new ArrayList<>();
    variables = new ArrayList<>();
    lastPlayedAt = ZonedDateTime.now();
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_test_configuration_with_default_values() {
      // WHEN
      TestConfiguration test = TestConfiguration.create(title, position, tags, variables);

      // THEN
      assertThat(test.getId()).isNotNull();
      assertThat(test.getTitle()).isEqualTo(title);
      assertThat(test.getPosition()).isEqualTo(position);
      assertThat(test.getStatus()).isEqualTo(ConfigurationStatus.NEW);
      assertThat(test.getTags()).isEqualTo(tags);
      assertThat(test.getVariables()).isEqualTo(variables);
      assertThat(test.getLastPlayedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_title_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestConfiguration.builder()
                      .testConfigurationId(TestConfigurationId.generate())
                      .title(null)
                      .position(position)
                      .status(status)
                      .tags(tags)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field TestTitle is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_position_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestConfiguration.builder()
                      .testConfigurationId(TestConfigurationId.generate())
                      .title(title)
                      .position(null)
                      .status(status)
                      .tags(tags)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field Position is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_status_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestConfiguration.builder()
                      .testConfigurationId(TestConfigurationId.generate())
                      .title(title)
                      .position(position)
                      .status(null)
                      .tags(tags)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description",
              "The field ConfigurationStatus is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_tags_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestConfiguration.builder()
                      .testConfigurationId(TestConfigurationId.generate())
                      .title(title)
                      .position(position)
                      .status(status)
                      .tags(null)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field Tags is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_variables_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  TestConfiguration.builder()
                      .testConfigurationId(TestConfigurationId.generate())
                      .title(title)
                      .position(position)
                      .status(status)
                      .tags(tags)
                      .variables(null)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field Variables is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    private TestConfiguration test;

    @BeforeEach
    void setUp() {
      test = TestConfiguration.create(title, position, tags, variables);
    }

    @Test
    void should_update_test_configuration() {
      // GIVEN
      TestTitle newTitle = new TestTitle("New Test Case");
      Position newPosition = new Position(2);
      List<Tag> newTags = new ArrayList<>();
      newTags.add(new Tag("new-tag"));
      List<Variable> newVariables = new ArrayList<>();
      newVariables.add(new Variable("new-var"));

      TestConfiguration newTest =
          TestConfiguration.builder()
              .testConfigurationId(TestConfigurationId.generate())
              .title(newTitle)
              .position(newPosition)
              .status(ConfigurationStatus.IN_PROGRESS)
              .tags(newTags)
              .variables(newVariables)
              .lastPlayedAt(lastPlayedAt)
              .build();

      // WHEN
      test.update(newTest);

      // THEN
      assertThat(test.getTitle()).isEqualTo(newTitle);
      assertThat(test.getPosition()).isEqualTo(newPosition);
      assertThat(test.getTags()).isEqualTo(newTags);
      assertThat(test.getVariables()).isEqualTo(newVariables);
    }
  }

  @Nested
  class ComparisonTests {

    private TestConfiguration test;

    @BeforeEach
    void setUp() {
      test = TestConfiguration.create(title, position, tags, variables);
    }

    @Test
    void should_detect_changes_in_title() {
      // GIVEN
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(new TestTitle("Different Title"))
              .position(test.getPosition())
              .status(test.getStatus())
              .tags(test.getTags())
              .variables(test.getVariables())
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_position() {
      // GIVEN
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(test.getTitle())
              .position(new Position(99))
              .status(test.getStatus())
              .tags(test.getTags())
              .variables(test.getVariables())
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_status() {
      // GIVEN
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(test.getTitle())
              .position(test.getPosition())
              .status(ConfigurationStatus.IN_PROGRESS)
              .tags(test.getTags())
              .variables(test.getVariables())
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_tags() {
      // GIVEN
      List<Tag> differentTags = new ArrayList<>();
      differentTags.add(new Tag("different-tag"));
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(test.getTitle())
              .position(test.getPosition())
              .status(test.getStatus())
              .tags(differentTags)
              .variables(test.getVariables())
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_variables() {
      // GIVEN
      List<Variable> differentVariables = new ArrayList<>();
      differentVariables.add(new Variable("different-var"));
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(test.getTitle())
              .position(test.getPosition())
              .status(test.getStatus())
              .tags(test.getTags())
              .variables(differentVariables)
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_return_false_when_no_changes() {
      // GIVEN
      TestConfiguration other =
          TestConfiguration.builder()
              .testConfigurationId(test.getId())
              .title(test.getTitle())
              .position(test.getPosition())
              .status(test.getStatus())
              .tags(test.getTags())
              .variables(test.getVariables())
              .lastPlayedAt(test.getLastPlayedAt())
              .build();

      // WHEN
      boolean hasChanged = test.hasChanged(other);

      // THEN
      assertThat(hasChanged).isFalse();
    }
  }
}
