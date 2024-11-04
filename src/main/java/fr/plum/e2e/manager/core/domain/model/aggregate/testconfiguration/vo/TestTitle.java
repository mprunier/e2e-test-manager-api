package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import fr.plum.e2e.manager.core.domain.model.exception.CharactersForbiddenException;
import fr.plum.e2e.manager.core.domain.model.exception.TitleEmptyException;
import org.apache.commons.lang3.StringUtils;

public record TestTitle(String value) {

  public TestTitle {
    if (StringUtils.isBlank(value)) {
      throw new TitleEmptyException();
    }
    if (value.contains("|") || value.contains(";")) {
      throw new CharactersForbiddenException();
    }
  }
}
