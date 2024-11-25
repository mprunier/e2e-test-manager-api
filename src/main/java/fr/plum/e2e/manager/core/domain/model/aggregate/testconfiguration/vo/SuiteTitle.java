package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_SUITE;

public record SuiteTitle(String value) {
  public static SuiteTitle noSuite() {
    return new SuiteTitle(NO_SUITE);
  }
}
