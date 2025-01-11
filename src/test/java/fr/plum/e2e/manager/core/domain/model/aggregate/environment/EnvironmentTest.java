package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentIsEnabled;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.MaxParallelWorkers;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.SourceCodeInformation;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.exception.HiddenVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.ActionUsername;
import fr.plum.e2e.manager.sharedkernel.domain.model.aggregate.AuditInfo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EnvironmentTest {

  private Environment environment;
  private EnvironmentDescription description;
  private SourceCodeInformation sourceCodeInfo;
  private List<EnvironmentVariable> variables;
  private AuditInfo auditInfo;
  private MaxParallelWorkers maxWorkers;
  private EnvironmentIsEnabled isEnabled;

  @BeforeEach
  void setUp() {
    // Given
    description = new EnvironmentDescription("Test Environment");
    sourceCodeInfo =
        SourceCodeInformation.builder()
            .projectId("project1")
            .token("token123")
            .branch("main")
            .build();
    variables = new ArrayList<>();
    variables.add(
        EnvironmentVariable.create(
            new EnvironmentVariableId("VAR1"),
            new VariableValue("value1"),
            new VariableDescription("Description 1"),
            new VariableIsHidden(false)));
    auditInfo = AuditInfo.create(new ActionUsername("testUser"), ZonedDateTime.now());
    maxWorkers = new MaxParallelWorkers(4);
    isEnabled = EnvironmentIsEnabled.enabled();

    environment =
        Environment.builder()
            .environmentId(EnvironmentId.generate())
            .environmentDescription(description)
            .sourceCodeInformation(sourceCodeInfo)
            .maxParallelWorkers(maxWorkers)
            .isEnabled(isEnabled)
            .variables(variables)
            .auditInfo(auditInfo)
            .build();
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_environment_with_valid_data() {
      // GIVEN
      // Test data already set up in setUp()

      // WHEN
      Environment newEnv = Environment.create(description, sourceCodeInfo, variables, auditInfo);

      // THEN
      assertThat(newEnv).isNotNull();
      assertThat(newEnv.getId()).isNotNull();
      assertThat(newEnv.getEnvironmentDescription()).isEqualTo(description);
      assertThat(newEnv.getSourceCodeInformation()).isEqualTo(sourceCodeInfo);
      assertThat(newEnv.getMaxParallelWorkers().value()).isEqualTo(1);
      assertThat(newEnv.getIsEnabled().value()).isTrue();
      assertThat(newEnv.getVariables()).isEqualTo(variables);
      assertThat(newEnv.getAuditInfo()).isEqualTo(auditInfo);
    }

    @Test
    void should_throw_exception_when_description_is_null() {
      // GIVEN
      // Test data already set up in setUp()

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  Environment.builder()
                      .environmentId(EnvironmentId.generate())
                      .environmentDescription(null)
                      .sourceCodeInformation(sourceCodeInfo)
                      .maxParallelWorkers(maxWorkers)
                      .isEnabled(isEnabled)
                      .variables(variables)
                      .auditInfo(auditInfo)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description",
              "The field environmentDescription is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_sourceCodeInfo_is_null() {
      // GIVEN
      // Test data already set up in setUp()

      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  Environment.builder()
                      .environmentId(EnvironmentId.generate())
                      .environmentDescription(description)
                      .sourceCodeInformation(null)
                      .maxParallelWorkers(maxWorkers)
                      .isEnabled(isEnabled)
                      .variables(variables)
                      .auditInfo(auditInfo)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description",
              "The field sourceCodeInformation is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    @Test
    void should_update_global_info_successfully() {
      // GIVEN
      EnvironmentDescription newDescription = new EnvironmentDescription("Updated Environment");
      SourceCodeInformation newSourceInfo =
          SourceCodeInformation.builder()
              .projectId("project2")
              .token("newToken")
              .branch("develop")
              .build();
      MaxParallelWorkers newMaxWorkers = new MaxParallelWorkers(8);

      // WHEN
      environment.updateGlobalInfo(newDescription, newSourceInfo, newMaxWorkers);

      // THEN
      assertThat(environment.getEnvironmentDescription()).isEqualTo(newDescription);
      assertThat(environment.getSourceCodeInformation()).isEqualTo(newSourceInfo);
      assertThat(environment.getMaxParallelWorkers()).isEqualTo(newMaxWorkers);
    }

    @Test
    void should_preserve_token_when_masked() {
      // GIVEN
      String originalToken = environment.getSourceCodeInformation().token();
      SourceCodeInformation maskedSourceInfo =
          SourceCodeInformation.builder()
              .projectId("project2")
              .token("****")
              .branch("develop")
              .build();

      // WHEN
      environment.updateGlobalInfo(description, maskedSourceInfo, maxWorkers);

      // THEN
      assertThat(environment.getSourceCodeInformation().token()).isEqualTo(originalToken);
    }
  }

  @Nested
  class VariablesTests {

    @Test
    void should_update_variables_successfully() {
      // GIVEN
      List<EnvironmentVariable> newVariables = new ArrayList<>();
      newVariables.add(
          EnvironmentVariable.create(
              new EnvironmentVariableId("VAR2"),
              new VariableValue("value2"),
              new VariableDescription("Description 2"),
              new VariableIsHidden(false)));

      // WHEN
      environment.updateVariables(newVariables);

      // THEN
      assertThat(environment.getVariables()).hasSize(1).isEqualTo(newVariables);
    }

    @Test
    void should_throw_exception_when_unhiding_masked_variable() {
      // GIVEN
      EnvironmentVariable hiddenVar =
          EnvironmentVariable.create(
              new EnvironmentVariableId("SECRET"),
              new VariableValue("secretValue"),
              new VariableDescription("Secret Variable"),
              new VariableIsHidden(true));
      List<EnvironmentVariable> varsWithHidden = new ArrayList<>();
      varsWithHidden.add(hiddenVar);
      environment.updateVariables(varsWithHidden);

      // WHEN / THEN
      List<EnvironmentVariable> updatedVars = new ArrayList<>();
      EnvironmentVariable attemptedUpdate =
          EnvironmentVariable.create(
              new EnvironmentVariableId("SECRET"),
              new VariableValue("**********"),
              new VariableDescription("Updated Secret"),
              new VariableIsHidden(false));
      updatedVars.add(attemptedUpdate);

      assertThatThrownBy(() -> environment.updateVariables(updatedVars))
          .isInstanceOf(HiddenVariableException.class);
    }
  }

  @Nested
  class AuditInfoTests {

    @Test
    void should_update_audit_info_successfully() {
      // GIVEN
      ActionUsername newUser = new ActionUsername("newUser");
      ZonedDateTime newTime = ZonedDateTime.now().plusHours(1);

      // WHEN
      environment.updateAuditInfo(newUser, newTime);

      // THEN
      assertThat(environment.getAuditInfo().getUpdatedBy()).isEqualTo(newUser);
      assertThat(environment.getAuditInfo().getUpdatedAt()).isEqualTo(newTime);
    }
  }
}
