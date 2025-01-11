package fr.plum.e2e.manager.sharedkernel.domain.assertion;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fr.plum.e2e.manager.sharedkernel.domain.exception.InvalidNumberValueException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.StringInvalidFormatException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AssertTest {

  @Test
  void shouldNotValidateNullObjectAsNotNull() {
    assertThatThrownBy(() -> Assert.notNull("fieldName", null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldValidateNotNullObject() {
    assertThatCode(() -> Assert.notNull("fieldName", "value")).doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateNullStringAsNotBlank() {
    assertThatThrownBy(() -> Assert.notBlank("fieldName", null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotValidateBlankStringAsNotBlank() {
    assertThatThrownBy(() -> Assert.notBlank("fieldName", "   "))
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldValidateNotBlankString() {
    assertThatCode(() -> Assert.notBlank("fieldName", "value")).doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateNullOptionalAsNotEmpty() {
    assertThatThrownBy(() -> Assert.notEmpty("field", null))
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotValidateEmptyOptionalAsNotEmpty() {
    assertThatThrownBy(() -> Assert.notEmpty("field", Optional.empty()))
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldValidateNotEmptyOptional() {
    assertThatCode(() -> Assert.notEmpty("field", Optional.of("value"))).doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateStringNotNull() {
    assertThatThrownBy(() -> Assert.field("fieldName", (String) null).notNull())
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotValidateStringWithWrongFormat() {
    assertThatThrownBy(() -> Assert.field("fieldName", "value").format("test"))
        .isExactlyInstanceOf(StringInvalidFormatException.class);
  }

  @Test
  void shouldNotValidateNonPositiveIntegerForNullValue() {
    assertThatThrownBy(() -> Assert.field("fieldName", (Integer) null).strictlyPositive())
        .isExactlyInstanceOf(MissingMandatoryValueException.class);
  }

  @Test
  void shouldNotValidateNonPositiveIntegerForZero() {
    assertThatThrownBy(() -> Assert.field("fieldName", 0).strictlyPositive())
        .isExactlyInstanceOf(InvalidNumberValueException.class);
  }

  @Test
  void shouldNotValidateNonPositiveIntegerForNegativeValue() {
    assertThatThrownBy(() -> Assert.field("fieldName", -1).strictlyPositive())
        .isExactlyInstanceOf(InvalidNumberValueException.class);
  }

  @Test
  void shouldValidateStrictlyPositiveInteger() {
    assertThatCode(() -> Assert.field("fieldName", 1).strictlyPositive())
        .doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateNegativeIntegerForPositive() {
    assertThatThrownBy(() -> Assert.field("fieldName", -1).positive())
        .isExactlyInstanceOf(InvalidNumberValueException.class);
  }

  @Test
  void shouldValidateZeroForPositive() {
    assertThatCode(() -> Assert.field("fieldName", 0).positive()).doesNotThrowAnyException();
  }

  @Test
  void shouldValidatePositiveInteger() {
    assertThatCode(() -> Assert.field("fieldName", 1).positive()).doesNotThrowAnyException();
  }

  @Test
  void shouldNotValidateNonZeroForZero() {
    assertThatThrownBy(() -> Assert.field("fieldName", 1).zero())
        .isExactlyInstanceOf(InvalidNumberValueException.class);
  }

  @Test
  void shouldValidateZero() {
    assertThatCode(() -> Assert.field("fieldName", 0).zero()).doesNotThrowAnyException();
  }

  @Test
  void shouldValidateFormatForNullStringValue() {
    assertThatCode(() -> Assert.field("field", (String) null).format("test"))
        .doesNotThrowAnyException();
  }

  @Test
  void shouldValidateFormatForExactSameString() {
    assertThatCode(() -> Assert.field("field", "value").format("value")).doesNotThrowAnyException();
  }

  @Test
  void shouldValidateFormatForStringContainingRegex() {
    assertThatCode(() -> Assert.field("field", "value").format(".*lu.*"))
        .doesNotThrowAnyException();
  }
}
