package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_SUITE;

import fr.plum.e2e.manager.core.domain.model.exception.CharactersForbiddenException;
import fr.plum.e2e.manager.core.domain.model.exception.SuiteNoTitleException;
import fr.plum.e2e.manager.core.domain.model.exception.TitleEmptyException;
import org.apache.commons.lang3.StringUtils;

public record SuiteTitle(String value) {

  public SuiteTitle {
    if (StringUtils.isBlank(value)) {
      throw new TitleEmptyException();
    }
    if (value.contains("|") || value.contains(";")) {
      throw new CharactersForbiddenException();
    }
    if (NO_SUITE.equals(value)) {
      throw new SuiteNoTitleException();
    }
  }
}
