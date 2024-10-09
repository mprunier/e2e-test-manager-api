package fr.njj.galaxion.endtoendtesting.domain.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstant {

  public static final String START_PATH = "cypress/e2e/";
  public static final String END_TEST_JS_PATH = ".cy.js";
  public static final String END_TEST_TS_PATH = ".cy.ts";
  public static final String SCREENSHOT_PATH = "cypress/screenshots/";
  public static final String NO_SUITE = "No Suite";
  public static final String GLOBAL_ENVIRONMENT_ERROR = "Global error on this environment";
  public static final String DISABLE_TAG = "disable-on-e2e-testing-manager";
  public static final String GROUP_FOR_PARALLELIZATION = "group-for-parallelization-";
}
