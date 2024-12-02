package fr.plum.e2e.manager.sharedkernel.domain.assertion;

import fr.plum.e2e.manager.sharedkernel.domain.exception.InvalidNumberValueException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.MissingMandatoryValueException;
import fr.plum.e2e.manager.sharedkernel.domain.exception.StringInvalidFormatException;
import java.util.Optional;

public final class Assert {

  private Assert() {}

  public static void notBlank(String field, String value) {
    notNull(field, value);

    if (value.isBlank()) {
      throw new MissingMandatoryValueException(field);
    }
  }

  public static void notNull(String field, Object value) {
    if (value == null) {
      throw new MissingMandatoryValueException(field);
    }
  }

  public static void notEmpty(String field, Optional<?> value) {
    notNull(field, value);

    if (value.isEmpty()) {
      throw new MissingMandatoryValueException(field);
    }
  }

  public static StringAsserter field(String field, String value) {
    return new StringAsserter(field, value);
  }

  public static IntegerAsserter field(String field, Integer value) {
    return new IntegerAsserter(field, value);
  }

  public static final class StringAsserter {

    private final String field;
    private final String value;

    private StringAsserter(String field, String value) {
      this.field = field;
      this.value = value;
    }

    public StringAsserter notNull() {
      Assert.notNull(field, value);
      return this;
    }

    public StringAsserter format(String regex) {
      if (value == null) {
        return this;
      }

      if (!value.matches(regex)) {
        throw StringInvalidFormatException.builder().field(field).regex(regex).value(value).build();
      }

      return this;
    }
  }

  public static final class IntegerAsserter {
    private final String field;
    private final Integer value;

    private IntegerAsserter(String field, Integer value) {
      this.field = field;
      this.value = value;
    }

    public IntegerAsserter positive() {
      notNull(field, value);

      if (value < 0) {
        throw InvalidNumberValueException.positive(field, value);
      }

      return this;
    }

    public IntegerAsserter strictlyPositive() {
      notNull(field, value);

      if (value <= 0) {
        throw InvalidNumberValueException.strictlyPositive(field, value);
      }

      return this;
    }

    public IntegerAsserter zero() {
      notNull(field, value);

      if (value != 0) {
        throw InvalidNumberValueException.zero(field, value);
      }

      return this;
    }
  }
}
