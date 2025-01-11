package fr.plum.e2e.manager.core.domain.model.aggregate.environment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.EnvironmentVariableId;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableDescription;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableIsHidden;
import fr.plum.e2e.manager.core.domain.model.aggregate.environment.vo.VariableValue;
import fr.plum.e2e.manager.core.domain.model.exception.HiddenVariableException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EnvironmentVariableTest {

  private EnvironmentVariableId id;
  private VariableValue value;
  private VariableDescription description;
  private VariableIsHidden isHidden;

  @BeforeEach
  void setUp() {
    // GIVEN
    id = new EnvironmentVariableId("DB_URL");
    value = new VariableValue("jdbc:postgresql://localhost:5432/db");
    description = new VariableDescription("Database connection URL");
    isHidden = new VariableIsHidden(false);
  }

  @Nested
  class CreationTests {

    @Test
    void should_create_environment_variable_with_valid_data() {
      // WHEN
      EnvironmentVariable variable = EnvironmentVariable.create(id, value, description, isHidden);

      // THEN
      assertThat(variable.getId()).isEqualTo(id);
      assertThat(variable.getValue()).isEqualTo(value);
      assertThat(variable.getDescription()).isEqualTo(description);
      assertThat(variable.getIsHidden()).isEqualTo(isHidden);
    }

    @Test
    void should_throw_exception_when_value_is_null() {
      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  EnvironmentVariable.builder()
                      .environmentVariableId(id)
                      .value(null)
                      .description(description)
                      .isHidden(isHidden)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field value is mandatory and cannot be empty or null.");
    }

    @Test
    void should_throw_exception_when_isHidden_is_null() {
      // WHEN / THEN
      assertThatThrownBy(
              () ->
                  EnvironmentVariable.builder()
                      .environmentVariableId(id)
                      .value(value)
                      .description(description)
                      .isHidden(null)
                      .build())
          .isInstanceOf(MissingMandatoryValueException.class)
          .hasFieldOrPropertyWithValue("title", "missing-mandatory-value")
          .hasFieldOrPropertyWithValue(
              "description", "The field isHidden is mandatory and cannot be empty or null.");
    }
  }

  @Nested
  class UpdateTests {

    @Test
    void should_update_non_hidden_variable() {
      // GIVEN
      EnvironmentVariable originalVar =
          EnvironmentVariable.create(id, value, description, isHidden);
      EnvironmentVariable newVar =
          EnvironmentVariable.create(
              id,
              new VariableValue("new-value"),
              new VariableDescription("new description"),
              new VariableIsHidden(false));

      // WHEN
      EnvironmentVariable updatedVar = originalVar.update(newVar);

      // THEN
      assertThat(updatedVar.getId()).isEqualTo(id);
      assertThat(updatedVar.getValue()).isEqualTo(newVar.getValue());
      assertThat(updatedVar.getDescription()).isEqualTo(newVar.getDescription());
      assertThat(updatedVar.getIsHidden()).isEqualTo(newVar.getIsHidden());
    }

    @Test
    void should_throw_exception_when_unhiding_masked_variable() {
      // GIVEN
      EnvironmentVariable hiddenVar =
          EnvironmentVariable.create(id, value, description, new VariableIsHidden(true));

      EnvironmentVariable newVar =
          EnvironmentVariable.create(
              id, new VariableValue("**********"), description, new VariableIsHidden(false));

      // WHEN / THEN
      assertThatThrownBy(() -> hiddenVar.update(newVar))
          .isInstanceOf(HiddenVariableException.class);
    }

    @Test
    void should_preserve_hidden_value_when_variable_remains_hidden() {
      // GIVEN
      VariableValue secretValue = new VariableValue("secret123");
      EnvironmentVariable hiddenVar =
          EnvironmentVariable.create(id, secretValue, description, new VariableIsHidden(true));

      EnvironmentVariable newVar =
          EnvironmentVariable.create(
              id,
              new VariableValue("**********"),
              new VariableDescription("updated description"),
              new VariableIsHidden(true));

      // WHEN
      EnvironmentVariable updatedVar = hiddenVar.update(newVar);

      // THEN
      assertThat(updatedVar.getId()).isEqualTo(id);
      assertThat(updatedVar.getValue()).isEqualTo(secretValue);
      assertThat(updatedVar.getDescription()).isEqualTo(newVar.getDescription());
      assertThat(updatedVar.getIsHidden()).isEqualTo(newVar.getIsHidden());
    }
  }

  @Nested
  class ValueMaskingTests {

    @Test
    void should_detect_masked_value() {
      // GIVEN
      EnvironmentVariable variable =
          EnvironmentVariable.create(id, new VariableValue("**********"), description, isHidden);

      // WHEN
      boolean isMasked = variable.isValueMasked();

      // THEN
      assertThat(isMasked).isTrue();
    }

    @Test
    void should_detect_unmasked_value() {
      // GIVEN
      EnvironmentVariable variable = EnvironmentVariable.create(id, value, description, isHidden);

      // WHEN
      boolean isMasked = variable.isValueMasked();

      // THEN
      assertThat(isMasked).isFalse();
    }
  }
}
