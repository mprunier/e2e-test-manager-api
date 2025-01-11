package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteConfigurationId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Variable;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SuiteConfigurationTest {

  private SuiteTitle title;
  private List<TestConfiguration> tests;
  private List<Tag> tags;
  private List<Variable> variables;
  private ZonedDateTime lastPlayedAt;
  private ConfigurationStatus status;

  @BeforeEach
  void setUp() {
    title = new SuiteTitle("Test Suite");
    tests = new ArrayList<>();
    tags = new ArrayList<>();
    variables = new ArrayList<>();
    lastPlayedAt = ZonedDateTime.now();
    status = ConfigurationStatus.NEW;

    tests.add(createTestConfiguration("Test 1"));
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_suite_configuration_with_default_values() {
      // WHEN
      SuiteConfiguration suite = SuiteConfiguration.create(title, tags, variables, tests);

      // THEN
      assertThat(suite.getId()).isNotNull();
      assertThat(suite.getTitle()).isEqualTo(title);
      assertThat(suite.getStatus()).isEqualTo(ConfigurationStatus.NEW);
      assertThat(suite.getTests()).isEqualTo(tests);
      assertThat(suite.getTags()).isEqualTo(tags);
      assertThat(suite.getVariables()).isEqualTo(variables);
      assertThat(suite.getLastPlayedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_title_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SuiteConfiguration.builder()
                      .suiteConfigurationId(SuiteConfigurationId.generate())
                      .title(null)
                      .status(status)
                      .tests(tests)
                      .tags(tags)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field title is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_tests_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SuiteConfiguration.builder()
                      .suiteConfigurationId(SuiteConfigurationId.generate())
                      .title(title)
                      .status(status)
                      .tests(null)
                      .tags(tags)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field tests is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_tags_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SuiteConfiguration.builder()
                      .suiteConfigurationId(SuiteConfigurationId.generate())
                      .title(title)
                      .status(status)
                      .tests(tests)
                      .tags(null)
                      .variables(variables)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field tags is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_variables_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  SuiteConfiguration.builder()
                      .suiteConfigurationId(SuiteConfigurationId.generate())
                      .title(title)
                      .status(status)
                      .tests(tests)
                      .tags(tags)
                      .variables(null)
                      .lastPlayedAt(lastPlayedAt)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field variables is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    private SuiteConfiguration suite;

    @BeforeEach
    void setUp() {
      suite = SuiteConfiguration.create(title, tags, variables, tests);
    }

    @Test
    void should_update_suite_configuration() {
      // GIVEN
      SuiteTitle newTitle = new SuiteTitle("New Title");
      List<Tag> newTags = new ArrayList<>();
      newTags.add(new Tag("new-tag"));
      List<Variable> newVariables = new ArrayList<>();
      newVariables.add(new Variable("new-var"));

      SuiteConfiguration newSuite =
          SuiteConfiguration.builder()
              .suiteConfigurationId(SuiteConfigurationId.generate())
              .title(newTitle)
              .status(ConfigurationStatus.IN_PROGRESS)
              .tests(new ArrayList<>())
              .tags(newTags)
              .variables(newVariables)
              .lastPlayedAt(ZonedDateTime.now())
              .build();

      // WHEN
      suite.update(newSuite);

      // THEN
      assertThat(suite.getTitle()).isEqualTo(newTitle);
      assertThat(suite.getTags()).isEqualTo(newTags);
      assertThat(suite.getVariables()).isEqualTo(newVariables);
    }
  }

  @Nested
  class ComparisonTests {

    private SuiteConfiguration suite;

    @BeforeEach
    void setUp() {
      suite = SuiteConfiguration.create(title, tags, variables, tests);
    }

    @Test
    void should_detect_changes_in_title() {
      // GIVEN
      SuiteConfiguration newSuite =
          SuiteConfiguration.create(new SuiteTitle("Different Title"), tags, variables, tests);

      // WHEN
      boolean hasChanged = suite.hasChanged(newSuite);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_tags() {
      // GIVEN
      List<Tag> newTags = new ArrayList<>();
      newTags.add(new Tag("new-tag"));
      SuiteConfiguration newSuite = SuiteConfiguration.create(title, newTags, variables, tests);

      // WHEN
      boolean hasChanged = suite.hasChanged(newSuite);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_variables() {
      // GIVEN
      List<Variable> newVariables = new ArrayList<>();
      newVariables.add(new Variable("new-var"));
      SuiteConfiguration newSuite = SuiteConfiguration.create(title, tags, newVariables, tests);

      // WHEN
      boolean hasChanged = suite.hasChanged(newSuite);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_detect_changes_in_tests() {
      // GIVEN
      List<TestConfiguration> newTests = new ArrayList<>();
      newTests.add(createTestConfiguration("Different Test"));
      SuiteConfiguration newSuite = SuiteConfiguration.create(title, tags, variables, newTests);

      // WHEN
      boolean hasChanged = suite.hasChanged(newSuite);

      // THEN
      assertThat(hasChanged).isTrue();
    }

    @Test
    void should_return_false_when_no_changes() {
      // GIVEN
      SuiteConfiguration newSuite = SuiteConfiguration.create(title, tags, variables, tests);

      // WHEN
      boolean hasChanged = suite.hasChanged(newSuite);

      // THEN
      assertThat(hasChanged).isFalse();
    }
  }

  private TestConfiguration createTestConfiguration(String title) {
    return TestConfiguration.create(
        new TestTitle(title),
        new Position(1),
        new ArrayList<>(), // tags
        new ArrayList<>() // variables
        );
  }
}
