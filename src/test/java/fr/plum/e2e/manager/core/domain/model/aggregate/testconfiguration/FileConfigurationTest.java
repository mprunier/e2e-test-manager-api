package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.DISABLE_TAG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.FileName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.GroupName;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Position;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.SuiteTitle;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.Tag;
import fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo.TestTitle;
import fr.plum.e2e.manager.core.domain.model.exception.TitleDuplicationException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FileConfigurationTest {

  private FileName fileName;
  private EnvironmentId environmentId;
  private GroupName group;
  private List<SuiteConfiguration> suites;
  private AuditInfo auditInfo;

  @BeforeEach
  void setUp() {
    // GIVEN
    fileName = new FileName("test-file.spec.js");
    environmentId = new EnvironmentId(UUID.randomUUID());
    group = new GroupName("test-group");
    auditInfo = AuditInfo.create(new ActionUsername("testUser"), ZonedDateTime.now());
    suites = new ArrayList<>();
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_file_configuration_with_default_values() {
      // WHEN
      FileConfiguration config =
          FileConfiguration.create(fileName, auditInfo, environmentId, group, suites);

      // THEN
      assertThat(config.getId()).isEqualTo(fileName);
      assertThat(config.getEnvironmentId()).isEqualTo(environmentId);
      assertThat(config.getGroup()).isEqualTo(group);
      assertThat(config.getSuites()).isEmpty();
      assertThat(config.getAuditInfo()).isEqualTo(auditInfo);
    }

    @Test
    void should_throw_exception_when_environmentId_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  FileConfiguration.builder()
                      .fileName(fileName)
                      .auditInfo(auditInfo)
                      .environmentId(null)
                      .group(group)
                      .suites(suites)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field environmentId is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_group_is_null() {
      // WHEN/THEN
      assertThatThrownBy(
              () ->
                  FileConfiguration.builder()
                      .fileName(fileName)
                      .auditInfo(auditInfo)
                      .environmentId(environmentId)
                      .group(null)
                      .suites(suites)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field group is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    private FileConfiguration config;

    @BeforeEach
    void setUp() {
      config = FileConfiguration.create(fileName, auditInfo, environmentId, group, suites);
    }

    @Test
    void should_update_file_configuration() {
      // GIVEN
      GroupName newGroup = new GroupName("new-group");
      List<SuiteConfiguration> newSuites =
          Arrays.asList(createSuiteConfiguration("suite1"), createSuiteConfiguration("suite2"));
      FileConfiguration newConfig =
          FileConfiguration.builder()
              .fileName(fileName)
              .auditInfo(auditInfo)
              .environmentId(environmentId)
              .group(newGroup)
              .suites(newSuites)
              .build();

      // WHEN
      config.update(newConfig);

      // THEN
      assertThat(config.getGroup()).isEqualTo(newGroup);
      assertThat(config.getSuites()).isEqualTo(newSuites);
    }
  }

  @Nested
  class ValidationTests {

    @Test
    void should_validate_unique_titles() {
      // GIVEN
      List<SuiteConfiguration> suitesWithUniqueTitles =
          Arrays.asList(createSuiteConfiguration("suite1"), createSuiteConfiguration("suite2"));
      FileConfiguration config =
          FileConfiguration.create(
              fileName, auditInfo, environmentId, group, suitesWithUniqueTitles);

      // WHEN/THEN
      config.validateUniqueTitles(); // Should not throw exception
    }

    @Test
    void should_throw_exception_for_duplicate_suite_titles() {
      // GIVEN
      List<SuiteConfiguration> suitesWithDuplicateTitles =
          Arrays.asList(createSuiteConfiguration("suite1"), createSuiteConfiguration("suite1"));
      FileConfiguration config =
          FileConfiguration.create(
              fileName, auditInfo, environmentId, group, suitesWithDuplicateTitles);

      // WHEN/THEN
      assertThatThrownBy(config::validateUniqueTitles)
          .isInstanceOf(TitleDuplicationException.class);
    }

    @Test
    void should_throw_exception_for_duplicate_test_titles_within_suite() {
      // GIVEN
      List<TestConfiguration> testsWithDuplicateTitles =
          Arrays.asList(createTestConfiguration(), createTestConfiguration());
      SuiteConfiguration suiteWithDuplicateTests =
          createSuiteConfiguration(testsWithDuplicateTitles);
      FileConfiguration config =
          FileConfiguration.create(
              fileName, auditInfo, environmentId, group, List.of(suiteWithDuplicateTests));

      // WHEN/THEN
      assertThatThrownBy(config::validateUniqueTitles)
          .isInstanceOf(TitleDuplicationException.class);
    }
  }

  @Nested
  class DisabledConfigurationsTests {

    @Test
    void should_remove_disabled_configurations() {
      // GIVEN
      List<Tag> enabledTags = new ArrayList<>(List.of(new Tag("enabled")));
      List<Tag> disabledTags = new ArrayList<>(List.of(new Tag(DISABLE_TAG)));

      TestConfiguration enabledTest = createTestConfiguration("test1", enabledTags);
      TestConfiguration disabledTest = createTestConfiguration("test2", disabledTags);

      List<TestConfiguration> testsForEnabledSuite = new ArrayList<>();
      testsForEnabledSuite.add(enabledTest);
      SuiteConfiguration enabledSuite =
          createSuiteConfiguration("suite1", testsForEnabledSuite, enabledTags);

      List<TestConfiguration> testsForDisabledSuite = new ArrayList<>();
      testsForDisabledSuite.add(disabledTest);
      SuiteConfiguration disabledSuite =
          createSuiteConfiguration("suite2", testsForDisabledSuite, disabledTags);

      List<SuiteConfiguration> allSuites = new ArrayList<>();
      allSuites.add(enabledSuite);
      allSuites.add(disabledSuite);

      FileConfiguration config =
          FileConfiguration.create(fileName, auditInfo, environmentId, group, allSuites);

      // WHEN
      config.removeDisabledConfigurations();

      // THEN
      assertThat(config.getSuites()).hasSize(1);
      assertThat(config.getSuites().getFirst().getTitle().value()).isEqualTo("suite1");
      assertThat(config.getSuites().getFirst().getTests()).hasSize(1);
      assertThat(config.getSuites().getFirst().getTests().getFirst().getTitle().value())
          .isEqualTo("test1");
    }

    @Test
    void should_return_true_when_has_only_disabled_configurations() {
      // GIVEN
      List<Tag> disabledTags = new ArrayList<>(List.of(new Tag(DISABLE_TAG)));
      TestConfiguration disabledTest = createTestConfiguration("test1", disabledTags);

      List<TestConfiguration> tests = new ArrayList<>();
      tests.add(disabledTest);

      SuiteConfiguration disabledSuite = createSuiteConfiguration("suite1", tests, disabledTags);

      List<SuiteConfiguration> suites = new ArrayList<>();
      suites.add(disabledSuite);

      FileConfiguration config =
          FileConfiguration.create(fileName, auditInfo, environmentId, group, suites);

      // WHEN
      boolean result = config.hasOnlyDisabledConfigurations();

      // THEN
      assertThat(result).isTrue();
    }
  }

  // Helper methods to create test data
  private SuiteConfiguration createSuiteConfiguration(String title) {
    return createSuiteConfiguration(title, new ArrayList<>(), new ArrayList<>());
  }

  private SuiteConfiguration createSuiteConfiguration(List<TestConfiguration> tests) {
    return createSuiteConfiguration("suite1", tests, new ArrayList<>());
  }

  private SuiteConfiguration createSuiteConfiguration(
      String title, List<TestConfiguration> tests, List<Tag> tags) {
    return SuiteConfiguration.create(new SuiteTitle(title), tags, new ArrayList<>(), tests);
  }

  private TestConfiguration createTestConfiguration() {
    return createTestConfiguration("test1", new ArrayList<>());
  }

  private TestConfiguration createTestConfiguration(String title, List<Tag> tags) {
    return TestConfiguration.create(new TestTitle(title), new Position(1), tags, new ArrayList<>());
  }
}
