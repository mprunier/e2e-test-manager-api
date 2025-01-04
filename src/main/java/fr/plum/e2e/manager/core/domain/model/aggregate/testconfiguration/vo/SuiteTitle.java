package fr.plum.e2e.manager.core.domain.model.aggregate.testconfiguration.vo;

import static fr.plum.e2e.manager.core.domain.constant.BusinessConstant.NO_SUITE;

import fr.plum.e2e.manager.sharedkernel.domain.assertion.Assert;

public record SuiteTitle(String value) {
  public SuiteTitle {
    Assert.notBlank("SuiteTitle value", value);
  }

  public static SuiteTitle noSuite() {
    return new SuiteTitle(NO_SUITE);
  }
}
